import { ref } from 'vue'
import { useJanusSessionStore } from '@/stores/janusSessionStore';
import { ChatMessageType, type ChatMessage } from '@/types/chat';

const server = import.meta.env.VITE_JANUS_WSS

let janus: any = null
let textPlugin: any = null
let videoPlugin: any = null
let audioPlugin: any = null

let currentVideoRoomId: number | null = null
let currentDisplayName: string | null = null
let isDataChannelOpen = false;
let dataOpenResolver: (() => void) | null = null;

const iceServers = [
  { urls: 'stun:stun.l.google.com:19302' }
  /*{
    urls: 'turn:127.0.0.1:3478',
    username: 'testuser',
    credential: 'testpass'
  }*/
]
//const iceServers = null

const Janus = (window as any).Janus
const mensajesRecibidosTextRoom = ref<any[]>([])
const mensajesRecibidosVideoRoom = ref<any[]>([])

const waitForDataChannel = (msg?: any) => new Promise<void>((res) => {
  //console.warn(`*** ${msg} ***`)
  dataOpenResolver = res;
});

// Inicializa la librería Janus
const initJanusLib = async (): Promise<void> => {
  return new Promise((resolve, reject) => {
    if (Janus.isInitialized) {
      console.log('[Janus] is already initialized:', Janus.isInitialized)
      return resolve()
    }
    Janus.init({
      debug: 'all',
      iceServers: iceServers,
      callback: () => {
        console.log('[Janus] intializing JanusLib')
        Janus.isInitialized = true
        resolve()
      },
      error: (err: any) => reject(err)
    })
  })
}

// Se conecta a un room TextRoom
const iniciarTextRoom = async (
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

// Se conecta a un room VideoRoom con DataChannel habilitado
const initJanusVideoRoom = (
  roomId: number,
  displayName: string
): Promise<void> => {
  return new Promise((resolve, reject) => {
    // 🚦 Verifica si ya estás conectado a ese mismo room como ese mismo usuario
    if (currentVideoRoomId === roomId && currentDisplayName === displayName && videoPlugin) {
      console.warn(`[VideoRoom] Ya estás conectado como ${displayName} en la sala ${roomId}`)
      resolve()
      return
    }
    if (!janus) {
      janus = new Janus({
        server,
        success: () => {
          console.log('[VideoRoom] init janus', janus)
          janus.attach({
            plugin: 'janus.plugin.videoroom',
            opaqueId: `videoroom-${roomId}`,
            success: (plugin: any) => {
              videoPlugin = plugin
              currentVideoRoomId = roomId
              currentDisplayName = displayName
              resolve()
            },
            onmessage: (msg: any, jsep: any) => {
              console.log('[VideoRoom] msg', msg)
              console.log('[VideoRoom] jsep', jsep)
              if (jsep) {
                //console.warn('[VideoRoom] handleRemoteJsep')
                videoPlugin.handleRemoteJsep({ jsep })
              } else {
                console.warn('[VideoRoom] No se recibió JSEP en onmessage')
              }
              if (msg.videoroom === 'joined') {
                console.log('[VideoRoom] Unido a la sala correctamente')
                if (msg.id) {
                  //memory.setFeedIdForAbogadoAndRoomId(displayName, roomId, msg.id)
                }
                //console.log('[VideoRoom] msg:', msg)
                videoPlugin.createOffer({
                  tracks: [{ type: "data" }],
                  trickle: true,
                  success: async (jsep: any) => {
                    console.log('[VideoRoom] offer created:', jsep)
                    videoPlugin.send({
                      message: {
                        request: 'publish',
                        audio: false,
                        video: false,
                        data: true
                      },
                      jsep: jsep,
                    })
                  },
                  error: (err: any) => {
                    console.error('[VideoRoom] Error creando offer:', err)
                  }
                })
              }
            },
            ondataopen: () => {
              console.log('[VideoRoom] DataChannel abierto ✅')
              isDataChannelOpen = true;
              if (dataOpenResolver) {
                dataOpenResolver();
                dataOpenResolver = null
              }
            },
            ondata: (data: any) => {
              console.warn('[VideoRoom] ondata')
              try {
                const msg = JSON.parse(data)
                console.log('[VideoRoom] Mensaje recibido:', msg)
                mensajesRecibidosVideoRoom.value.push(msg.data)
              } catch (e) {
                console.error('[VideoRoom] Error al parsear mensaje:', e)
              }
            },
            iceState: (state: any) => {
              console.log('[VideoRoom] IceState changed to:', state)
            },
            webrtcState: (active: boolean) => {
              console.log('[VideoRoom] WebRTC PeerConnection is ' + (active ? 'up' : 'down') + ' now.');
            },
            /*consentDialog: (on: any) => {},
            mediaState: (medium: any, on: boolean) => {},
            onlocalstream: (stream: any) => {},
            onremotestream: (stream: any) => {},*/
            error: (err: any) => {
              console.log('[VideoRoom] init error:', err)
              reject(err)
            }
          })
        }
      })
    }
  })
}

// Enviar mensaje por TextRoom
const enviarMensajeTextRoom = (msg: any) => {
  if (!textPlugin) return
  textPlugin.data({
    text: JSON.stringify(msg),
    error: (err: any) => {
      console.error('[TextRoom] Error al enviar mensaje:', err)
    }
  })
}

// Enviar mensaje por VideoRoom
const enviarMensajeVideoRoom = (mensajeConsulta: any): Promise<void> => {
  return new Promise((resolve, reject) => {
    if (!videoPlugin || !isDataChannelOpen) {
      console.warn('[VideoRoom] No hay una conexión activa para enviar mensaje.')
      reject()
    }

    console.warn('[VideoRoom] Enviando mensaje:', mensajeConsulta)

    /*
    type	          ¿Qué representa?
    "text"	        Un mensaje de texto simple de chat
    "consulta"	    Un mensaje relacionado con una consulta legal
    "status"	      Un cambio de estado (ej. aceptado, rechazado)
    "command"	      Instrucción para realizar una acción (ej. finalizar sesión)
    "typing"	      Indicador de "escribiendo..."
    "alert"	        Notificación urgente
    "ping"/"pong"	  Mensajes de latencia
    --------------------------------------------------------------------------
    type	          Propósito
    "consulta"	    Mensaje inicial o actualización de una consulta legal
    "respuesta"	    Respuesta del abogado a una consulta
    "confirmacion"	Confirmación de pago o aceptación
    "finalizar"	    El abogado o cliente da por terminada la sesión
    "notificacion"	Notificación informativa
    "chat"	        Mensaje informal entre cliente y abogado
    "archivo"	      Se comparte un archivo (se envía URL o metadatos)
    "typing"	      Indicador de "escribiendo..." en el chat
    --------------------------------------------------------------------------
    */

    const payload = {
      type: 'consulta', // puedes usar 'text', 'command', etc. según tu lógica
      data: {
        id: mensajeConsulta.id,
        title: mensajeConsulta.title,
        status: mensajeConsulta.status,
        desc: mensajeConsulta.desc,
        isPayed: mensajeConsulta.isPayed,
        roomId: mensajeConsulta.roomId,
        userFrom: {
          email: mensajeConsulta.userFrom.email,
          name: mensajeConsulta.userFrom.name
        },
        userTo: {
          email: mensajeConsulta.userTo.email,
          name: mensajeConsulta.userTo.name,
          roomId: mensajeConsulta.userTo.roomId
        },
        timestamp: Date.now()
      }
    };

    videoPlugin.data({
      text: JSON.stringify(payload),
      success: () => {
        console.log('[VideoRoom] mensaje enviado ✅')
        resolve()
      },
      error: (err: any) => {
        console.error('[VideoRoom] Error al enviar mensaje:', err)
        reject(err)
      }
    })
  })
}

// Verifica si una sala existe (videoroom)
const verificarSalaVideoRoomExiste = (
  roomId: number
): Promise<boolean> => {
  console.log('[VideoRoom] verifica sala:', roomId)
  return new Promise((resolve, reject) => {
    if (!janus) {
      reject('Janus no está inicializado aún')
      return (false)
    }
    console.log('[VideoRoom] verifica sala - janus:', janus)
    janus.attach({
      plugin: 'janus.plugin.videoroom',
      success: (plugin: any) => {
        plugin.send({
          message: { request: 'exists', room: roomId },
          success: (response: any) => {
            resolve(response.exists === true)
          },
          error: (err: any) => {
            console.error('[VideoRoom] Error al verificar sala:', err)
            plugin.detach()
            reject(err)
          }
        })
      },
      error: (err: any) => {
        console.log('[VideoRoom] verifica sala - error:', err)
        reject(err)
      }
    })
  })
}

const unirseSalaVideoRoomExistente = (
  register: any,
  onReady?: () => Promise<void> // callback opcional
): Promise<void> => {
  return new Promise((resolve, reject) => {
    const roomId = register.roomId
    const displayName = register.displayName
    const ptype = register.ptype
    console.log('[VideoRoom] joining room:', roomId, displayName)
    // Join as publisher
    /*const register = {
      request: 'join',
      room: roomId,
      ptype: ptype,
      display: displayName
    }*/
    console.warn('[Video_Room] register:', register)
    videoPlugin.send({
      message: register,
      success: () => {
        console.log('[VideoRoom] join success')
        /*waitForDataChannel().then(async() => {
          console.log('[VideoRoom DataChannel listo')
          if(onReady) {
            await onReady()
          }
          resolve(true)
        })*/
        const waitInterval = setInterval(() => {
          if (isDataChannelOpen) {
            clearInterval(waitInterval);
            console.log('[VideoRoom] DataChannel listo');
            if (onReady) onReady(); // Ejecuta callback
          }
        }, 1000)
        resolve()

      },
      error: (err: any) => {
        console.error('[VideoRoom join error', err)
        reject(err)
      }
    })
  })
}

// Crear sala si no existe
const crearSalaVideoRoomSiNoExiste = (
  roomId: number,
  options?: {
    description?: string,
    publishers?: number,
    bitrate?: number
  }
): Promise<void> => {
  console.log('[VideoRoom] create sala:', roomId, options)
  return new Promise((resolve, reject) => {
    if (!janus) {
      janus = new Janus({ server })
    }
    janus.attach({
      plugin: 'janus.plugin.videoroom',
      success: (plugin: any) => {
        const message = {
          request: 'create',
          room: roomId,
          description: options?.description || `Sala ${roomId}`,
          publishers: options?.publishers || 2,
          bitrate: options?.bitrate || 128000,
          audiolevel_event: false,
          videoorient_ext: false,
          notify_joining: false
        }
        plugin.send({
          message,
          success: () => {
            console.log('[VideoRoom] sala creada')
            //plugin.detach()
            resolve()
          },
          error: (err: any) => {
            console.log('[VideoRoom] error:', err)
            const msg = err.error || JSON.stringify(err)
            if (msg.includes('already exists')) {
              plugin.detach()
              resolve()
            } else {
              reject(err)
            }
          }
        })
      },
      error: (err: any) => {
        reject(err)
      }
    })
  })
}

const crearSalaTextRoom = async (
  roomId: number,
  options?: {
    description?: string,
    permanent?: boolean
  }
): Promise<void> => {
  return new Promise((resolve, reject) => {
    console.log('[Janus_Init] create sala:', roomId, options)
    janus = new Janus({
      server,
      iceServers,
      success: () => {
        console.log('[JanusInit] init janus', janus)
        janus.attach({
          plugin: 'janus.plugin.textroom',
          success: (plugin: any) => {
            const message = {
              request: 'create',
              room: roomId,
              description: options?.description || `Sala ${roomId}`,
              permanent: options?.permanent || false
            }
            plugin.send({
              message,
              success: () => {
                console.log('[Janus_Init] sala creada: ', roomId)
                resolve()
              },
              error: (err: any) => {
                console.log('[JAnus_Init] error:', err)
                const msg = err.error || JSON.stringify(err)
                if (msg.includes('already exists')) {
                  //plugin.detach()
                  //resolve()
                } else {
                  console.log('[Janus_Init] error:', err)
                  //reject(err)
                }
              }
            })
          },
          error: (err: any) => {
            console.log('[JAnus_Init] error:', err)
            //reject(err)
          }
        })
      },
      error: (err: any) => {
        console.log('[Janus_Init] error', err)
      }
    })
  })
}

const enviarMensajeTextRoomPorChat = (chatId: string, content: any) => {
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
  store.removeSession(chatId);
};

const desconectar = () => {
  if (textPlugin) {
    textPlugin.hangup()
    textPlugin.detach()
    textPlugin = null
  }
  if (videoPlugin) {
    videoPlugin.hangup()
    videoPlugin.detach()
    videoPlugin = null
  }
  if (janus) {
    janus.destroy()
    janus = null
  }
  mensajesRecibidosTextRoom.value = []
  mensajesRecibidosVideoRoom.value = []
  currentVideoRoomId = null
  currentDisplayName = null
}

export const useJanus = () => ({
  initJanusLib,

  iniciarTextRoom,
  crearSalaTextRoom,
  enviarMensajeTextRoom,
  mensajesRecibidosTextRoom,


  desconectar
})
