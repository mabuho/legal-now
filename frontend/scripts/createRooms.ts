/**
 * -------------------------------------------------------------
 *  Temporary helper to PRE‑CREATE and keep alive a TextRoom +
 *  AudioBridge room in Janus, written in **TypeScript**.
 * -------------------------------------------------------------
 *  Prerequisitos:
 *    npm i -D typescript ts-node @types/ws
 *    npm i ws uuid
 *
 *  Ejecución:
 *    JANUS_WSS=wss://<tu-ngrok>/janus JANUS_ROOM=4242 \
 *    npx ts-node ./scripts/createRooms.ts
 *
 *  Detén el script con Ctrl‑C. Los rooms se crean con
 *  `permanent:false`; se esfumarán al acabar la sesión.
 */

import WebSocket from 'ws';
import { v4 as uuid } from 'uuid';

/* --------------------------------------------------
 * Utilidades
 * --------------------------------------------------*/
interface JanusBaseMsg {
  janus: string;
  transaction?: string;
  session_id?: number;
  handle_id?: number;
  [key: string]: unknown | any;
}

//const JANUS_WSS: string = 'wss://a305-2806-2f0-9f80-eebe-edd4-2f04-3d01-133f.ngrok-free.app';
const JANUS_WSS: string = 'wss://306ed936fce1.ngrok-free.app';
const JANUS_WSS_PROTOCOL = 'janus-protocol'
const ROOM_ID: number = Number(12345);

if (!JANUS_WSS) {
  console.error('❌  Debes definir la variable de entorno JANUS_WSS');
  process.exit(1);
}

console.log("[WS] Creating WS Janus:", JANUS_WSS)
const ws = new WebSocket(JANUS_WSS, JANUS_WSS_PROTOCOL);
let sessionId: number | undefined;
let textHandleId: number | undefined;
let audioHandleId: number | undefined;

/** Envía un mensaje asegurando tener transaction unique */
function tx(msg: JanusBaseMsg) {
  msg.transaction = uuid();
  ws.send(JSON.stringify(msg));
}

/* --------------------------------------------------
 * Conexión & flujo
 * --------------------------------------------------*/
ws.on('open', () => {
  console.log('[WS] Conectado → creando sesión');
  tx({ janus: 'create' });
});

ws.on('message', (data: WebSocket.RawData) => {
  const msg = JSON.parse(data.toString()) as JanusBaseMsg;
  const { janus: type } = msg;

  if (type === 'error') {
    console.error('[JANUS] Error:', msg.error);
    process.exit(1);
  }

  if (type === 'success' && !sessionId) {
    sessionId = (msg as any).data.id as number;
    console.log('[JANUS] Session', sessionId);
    tx({ janus: 'attach', session_id: sessionId, plugin: 'janus.plugin.textroom' });
    return;
  }

  if (type === 'success' && sessionId && !textHandleId && (msg as any).data?.id) {
    textHandleId = (msg as any).data.id;
    console.log('[JANUS] TextRoom handle', textHandleId);
    tx({
      janus: 'message',
      session_id: sessionId,
      handle_id: textHandleId,
      body: {
        request: 'create',
        room: ROOM_ID,
        description: 'PoC chat',
        is_private: false,
        permanent: false,
        history: 100
      },
    });
    return;
  }

  if (type === 'event' && (msg.plugindata && msg.plugindata.data)) {
    const pdata = msg.plugindata.data as Record<string, unknown>;

    // TextRoom creado ➜ attach AudioBridge
    if (pdata.created === 'created' && !audioHandleId) {
      console.log('[TextRoom] Room', ROOM_ID, 'creado');
      tx({ janus: 'attach', session_id: sessionId!, plugin: 'janus.plugin.audiobridge' });
      return;
    }

    // AudioBridge creado
    if (pdata.audiobridge === 'created') {
      console.log('[AudioBridge] Room', ROOM_ID, 'creado');
      audioHandleId = msg.sender as number;
      console.log('✅  ¡Ambas salas listas! Manteniendo sesión viva…');
      return;
    }
  }
});

/* --------------------------------------------------
 * Keep‑alive & cleanup
 * --------------------------------------------------*/
const keepAlive = setInterval(() => {
  if (sessionId) tx({ janus: 'keepalive', session_id: sessionId });
}, 25_000);

process.on('SIGINT', () => {
  console.log('\nCerrando…');
  clearInterval(keepAlive);
  if (sessionId) tx({ janus: 'destroy', session_id: sessionId });
  setTimeout(() => process.exit(0), 1_000);
});
