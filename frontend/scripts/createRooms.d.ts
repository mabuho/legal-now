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
export {};
