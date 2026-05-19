import { ref } from 'vue';

export const useJanusRoom = () => {
    
  let janus: any = null
  let pluginHandle: any

  const mensajesRecibidos = ref<any[]>([])

  const initJanus = async (roomId: number, display: string) => {
    console.log('[Janus] intJanusRoom for:', roomId, display)
    const Janus = (window as any).Janus
    janus = ref<typeof Janus | null>(null)
    return new Promise<void>((resolve, reject) => {
      Janus.init({
        debug: 'all',
        callback: () => {
          janus.value = new Janus({
            server: import.meta.env.VITE_JANUS_WSS,
            iceServers: null,
            success: () => {
              janus.value.attach({
                plugin: 'janus.plugin.videoroom',
                success: (handle: any) => {
                  pluginHandle = handle

                  const joinRequest = {
                    request: 'join',
                    room: roomId,
                    ptype: 'publisher',
                    display
                  }

                  pluginHandle.send({ message: joinRequest })
                  resolve()
                },
                ondataopen: () => {
                  console.log('[DataChannel] abierto ✅')
                },
                ondata: (data: any) => {
                  const payload = JSON.parse(data)
                  console.log('[Janus] DataChannel - mensaje recibido:', payload)
                  mensajesRecibidos.value.push(payload)
                },
                error: (err: unknown) => {
                  console.error('Error Janus:', err)
                  reject(err)
                }
              })
            },
            error: reject
          })
        }
      })
    })
  }

  const enviarMensaje = async (msg: any) => {
    if (pluginHandle) {
        console.warn('[Janus] No hayconexión activa para enviar mensaje.')
    }
    console.log('[Janus] pluginHandle sendMessage:', pluginHandle)
    pluginHandle.data({
        text: JSON.stringify(msg),
        error: (err: any) => {
            console.error('[DataChannel] Error:', err)
        }
    })
  }

  const crearSalaSiNoExiste = async (
    roomId: number,
    options?: {
      description?: string
      publishers?: number
      bitrate?: number
    }
  ) => {
    // Envía a Janus mensaje tipo "create"
    return new Promise<void>((resolve, reject) => {
        if (!janus) {
          reject('Janus no está inicializado aún')
          return
        }
  
        janus.value.attach({
          plugin: 'janus.plugin.videoroom',
          success: (handle: any) => {
            pluginHandle = handle
  
            const message = {
              request: 'create',
              room: roomId,
              description: options?.description || `Sala ${roomId}`,
              publishers: options?.publishers || 1,
              bitrate: options?.bitrate || 128000,
              notify_joining: true // importante si quieres saber cuándo entra otro usuario
            }

            console.log('[Janus] creating room:', message)
  
            pluginHandle.send({
              message,
              success: (result: any) => {
                console.log('[Janus] Sala creada:', result)
                pluginHandle.hangup()
                pluginHandle.detach()
                resolve()
              },
              error: (err: any) => {
                const message = err.error || JSON.stringify(err)
                // Si el error es que ya existe, lo tomamos como válido
                if (message.includes('already exists')) {
                  console.warn('[Janus] Sala ya existe:', roomId)
                  pluginHandle.hangup()
                  pluginHandle.detach()
                  resolve()
                } else {
                  console.error('[Janus] Error al crear sala:', err)
                  reject(err)
                }
              }
            })
          },
          error: (err: any) => {
            console.error('[Janus] Error al adjuntar plugin para crear sala:', err)
            reject(err)
          },
          onmessage: () => {},
          ondataopen: () => {},
          ondata: () => {},
          oncleanup: () => {}
        })
      })
  }

  const verificarSalaExiste = async (roomId: number): Promise<boolean> => {
    return new Promise((resolve, reject) => {
      if (!janus) {
        reject('Janus no está inicializado aún')
        return
      }

      janus.value.attach({
        plugin: 'janus.plugin.videoroom',
        success: (handle: any) => {
          pluginHandle = handle
          const message = {
            request: 'exists',
            room: roomId
          }

          pluginHandle.send({
            message,
            success: (response: any) => {
              const existe = response.exists === true
              pluginHandle.hangup()
              pluginHandle.detach()
              resolve(existe)
            },
            error: (err: any) => {
              pluginHandle.hangup()
              pluginHandle.detach()
              console.error('[Janus] Error al verificar sala:', err)
              reject(err)
            }
          })
        },
        error: (err: any) => {
          console.error('[Janus] Error al adjuntar plugin para verificar sala:', err)
          reject(err)
        },
        onmessage: () => {},
        ondataopen: () => {},
        ondata: () => {},
        oncleanup: () => {}
      })
    })
  }

  /** Limpia la conexión actual */
  const desconectar = () => {
    if (pluginHandle) {
      pluginHandle.hangup()
      pluginHandle.detach()
    }
    if (janus) {
      janus.destroy()
      janus.value = null
    }
    mensajesRecibidos.value = []
  }

  return {
    initJanus,
    enviarMensaje,
    mensajesRecibidos,
    crearSalaSiNoExiste,
    verificarSalaExiste,
    desconectar
  }
}
