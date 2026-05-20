import { ref } from 'vue'
import { useJanusSessionStore } from '@/stores/janusSessionStore';
import { ChatMessageType, type ChatMessage } from '@/types/chat';

const server = import.meta.env.VITE_JANUS_WSS
//const janusSecre = import.meta.env.VITE_JANUS_API_SECRET
const iceServers = null
const debugLevel = 'all' // útil en pruebas
const plugin = 'janus.plugin.textroom'
let janus: any = null
const Janus = (window as any).Janus

export const initJanusLib = async (): Promise<void> => {
    return new Promise((resolve, reject) => {
        if (Janus.isInitialized) {
            console.log('[Janus] is already initialized:', Janus.isInitialized)
            return resolve()
        }
        Janus.init({
            debug: debugLevel,
            iceServers,
            callback: () => {
                console.log('[Janus] intializing JanusLib')
                Janus.isInitialized = true
                resolve()
            },
            error: (err: any) => reject(err)
        })
    })
}

export const iniciarTextRoom = async (
    chatId: string,
    roomId: number,
    user: any
): Promise<void> => {
    const janusStore = useJanusSessionStore();

    console.log('[Janus_Init] Setting up TextRoom.')
    const textHandle = ref<any | null>(null)
    /* Attach textroom */
    janus = new Janus({
        server,
        success: () => {
            console.log('[JanusInit] init janus', janus)
            janus.attach({
                plugin: 'janus.plugin.textroom',
                opaqueId: `textroom-${roomId}`,
                /* 'attach' Ok */
                success(pluginHandle: any) {
                    console.log('[Janus_Init] attach success ----')
                    /* Send 'setup' */
                    if (!pluginHandle) {
                        console.error('[Janus_Init] Plugin no adjuntado!')
                        return;
                    }
                    textHandle.value = pluginHandle;
                    console.log('[Janus_Init] TextHandle', textHandle.value.id)
                    console.log('[Janus_Init] Sending setup.')
                    textHandle.value?.send({ message: { request: 'setup' } })

                    janusStore.addSession(chatId, {
                        janus,
                        plugin: pluginHandle,
                        roomId,
                        userId: user.email,
                        mensajes: []
                    });
                },
                error(error: any) {
                    console.error('[Janus_Init] DataChannel error', error);
                },
                iceState(state: any) {
                    console.log('[Janus_Init] IceState changed to:', state)
                },
                mediaState(medium: any, on: boolean) {
                    console.log('[Janus_Init] ' + (on ? 'started' : 'stopped') + ' receiving our ' + medium + '.')
                },
                webrtcState(active: boolean) {
                    console.log('[Janus_Init] WebRTC PeerConnection is ' + (active ? 'up' : 'down') + ' now.');
                },
                /* onmessage' SDP offer arrives */
                onmessage(msg: any, jsep: any) {
                    console.log('[Janus_Init] onmessage ----')
                    console.log('[Janus_Init] msg', msg)
                    console.log('[Janus_Init] jsep', jsep)
                    if (jsep) {
                        textHandle.value.createAnswer({
                            jsep,
                            tracks: [{ type: 'data' }],
                            //media: { audio: false, video: false, data: true },
                            success: (jsep: any) => {
                                console.log('[Janus_Init] createAnswer success ---- sending ack ----')
                                let body = { request: "ack" };
                                textHandle.value.send({ message: body, jsep: jsep })
                            },
                            error: (err: any) => {
                                console.error('[Janus_Init] SDP answer error', err)
                            }
                        })
                    }
                    if (msg?.error) {
                        console.error('[Janus_Init] Message error:', msg.error)
                    }
                },
                /* DataChanel ready! */
                ondataopen() {
                    console.log('[Janus_Init] DataChannel is available! Joining roomId', roomId)
                    const join = {
                        textroom: 'join',
                        transaction: (window as any).Janus.randomString(12),
                        room: roomId,
                        username: user.email,
                        display: user.name
                    };
                    textHandle.value!.data({ text: JSON.stringify(join) });

                },
                /* The messages arrive DataChannel */
                ondata(data: any) {
                    const janusMsg = JSON.parse(data);

                    if (janusMsg.textroom && janusMsg.textroom !== 'message') {
                        console.warn('[Janus_Init] janus-message:', janusMsg)
                        return
                    }

                    const parsedMsg = JSON.parse(janusMsg.text)
                    console.warn('[saving_message] janusLib:onData... ')
                    const chatMessage: ChatMessage = {
                        uuid: parsedMsg.uuid || crypto.randomUUID(),
                        text: parsedMsg.text,
                        username: parsedMsg.username,
                        date: parsedMsg.date,
                        type: parsedMsg.type || ChatMessageType.TEXT,
                    }

                    janusStore.sessions[chatId].mensajes.push(chatMessage);

                    // Actualizar el store del chat
                    import('@/stores/chatSessionStore')
                        .then(({ useChatSessionStore }) => {
                            const chatStore = useChatSessionStore()
                            chatStore.saveChatMessage(chatId, chatMessage, false)
                        })
                },
                oncleanup: () => {
                    console.log('[Janus_Init] Handle textRoom clean up...')
                },
            });
        }
    })
}


export const enviarMensajeTextRoomPorChat = (chatId: string, content: any) => {
    console.warn('[saving_message] enviarMensajeTextRoomPorChat... ')
    const store = useJanusSessionStore();
    const session = store.getSession(chatId);
    if (!session || !session.plugin) return;

    const payload = {
        textroom: 'message',
        transaction: (window as any).Janus.randomString(12),
        room: session.roomId,
        text: JSON.stringify(content),
    };

    session.plugin.data({ text: JSON.stringify(payload) });
};

export const cerrarSesionTextRoomChat = (chatId: string) => {
    const store = useJanusSessionStore();
    const session = store.getSession(chatId);
    if (session) {
        try { session.plugin?.detach(); } catch (_) {}
        try { session.janus?.destroy(); } catch (_) {}
    }
    store.removeSession(chatId);
};
