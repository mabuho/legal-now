import { createClient } from 'redis';
import cors from 'cors';
import express from 'express';

type Request = import('express').Request;
type Response = import('express').Response;

const app = express();
const apiRouter = express.Router();

const ErrorResponse = {
  code: '',
  message: '',
  data: {}
}

const redis = createClient({
  url: process.env.REDIS_SERVER_URL
});

redis.on('error', (err: Error) => {
  console.error('[Redis] error:', err);
});

(async () => {
  try {
    await redis.connect();
    console.log('[Redis] Connected. ✅');
  } catch (err) {
    console.error('[Redis] Connection error:', err);
  }
})();

const consultationKeyPrefix = (
  { prefix = 'consultations:', key }: { prefix?: string, key: string }
): string => {
  return prefix.concat(key)
}

const statusKeyPrefix = (
  { prefix = 'status:', key }: { prefix?: string, key: string }
): string => {
  return prefix.concat(key)
}

const chatSessionKeyPrefix = (
  { prefix = 'chatSession:', key }: { prefix?: string, key: string }
): string => {
  return prefix.concat(key)
}

const chatMessagesKey = (
  { prefix = 'chatMessages:', key }: { prefix?: string, key: string }
): string => {
  return prefix.concat(key)
}

// Guardar consulta (POST) sin duplicados
apiRouter.post('/consultations', async (req: Request, res: Response) => {
  const { key, field, consulta } = req.body
  if (!key || !field || !consulta) {
    return res.status(400).json({ success: false, message: 'Favor de verificar los datos de la consulta.' })
  }

  const rKey = consultationKeyPrefix({ key })
  const exists = await redis.hExists(rKey, field)
  if (exists) {
    // Si ya existe, tenemos que acualizarla?
    return res.status(409).json({ success: false, message: 'La consulta ya existe' })
  }
  await redis.hSet(rKey, field, consulta)
  res.status(201).json({ success: true, message: "Consulta creada con exito." })
})

// Obtener todas las consultas por: email, consultaId (GET)
apiRouter.get('/consultations/:email', async (req: Request, res: Response) => {
  const { email } = req.params
  const data = await redis.hVals(email)
  const consultas = data.map((json) => JSON.parse(json))
  res.json(consultas)
})

// Obtener todas las consultas (GET) /*This could be used by Admin*/
/*apiRouter.get('/consultations', async (req: Request, res: Response) => {
  const data = await redis.hVals('consultations')
  const consultas = data.map((json) => JSON.parse(json))
  res.json(consultas)
})*/

// Actualizar consulta (PUT)
apiRouter.put('/consultations', async (req: Request, res: Response) => {
  const { key, field, consulta } = req.body
  const rKey = consultationKeyPrefix({ key })
  const exists = await redis.hExists(rKey, field)
  if (!exists) {
    return res.status(404).json({ success: false, message: 'Consulta no encontrada' })
  }
  await redis.hSet(rKey, field, consulta)
  res.json({ success: true, message: "Consulta actualizada con exito." })
})

// Eliminar consulta (DELETE)
apiRouter.delete('/consultations', async (req: Request, res: Response) => {
  const { key, field } = req.body
  const rKey = consultationKeyPrefix({ key })
  const removed = await redis.hDel(rKey, field)
  if (removed === 0) {
    return res.status(404).json({ success: false, message: 'Consulta no encontrada' })
  }
  res.json({ success: true, message: "Consulta eliminada con exito." })
})



// Obtener todas las consultas por estatus (GET)
apiRouter.get('/consultations/status/:status/email/:email', async (req: Request, res: Response) => {
  const { status, email } = req.params
  const rStatus = statusKeyPrefix({ key: status })
  const rKey = consultationKeyPrefix({ key: email })
  let data: any[] = []

  const ids = await redis.sMembers(rStatus)
  if (ids) {
    const rawConsultas = await redis.hGetAll(rKey)
    data = Object.entries(rawConsultas)
      .filter(([field]) => { return ids.includes(field) })
      .map(([, value]) => {
        const val: {} = value || {}
        if (typeof val === 'string') {
          try {
            return JSON.parse(val.toString())
          } catch (e) {
            console.error(`[WARN] Consulta malformada (string): ${val}`)
            return null
          }
        } else if (typeof val === 'object' && val !== null) {
          return val // ya es objeto
        } else {
          console.warn(`[WARN] Valor inesperado:`, JSON.parse(val.toString()))
          return null
        }
      })
      .filter(c => c !== null)
  }
  const consultas = data.map((json) => json)
  res.json(consultas)
})

apiRouter.post('/status/add', async (req: Request, res: Response) => {
  const { id, status } = req.body
  if (!id || !status) {
    return res.status(400).json({ success: false, message: 'Favor de verificar los datos de la consulta.' })
  }

  const rStatus = statusKeyPrefix({ key: status })

  const ids = await redis.sMembers(rStatus)
  if (ids) {
  }

  redis.sAdd(rStatus, id)

  res.status(201).json({ success: true, message: 'Registro de estatus exisoto.' })

})

apiRouter.post('/status/remove', async (req: Request, res: Response) => {
  const { id, status } = req.body
  if (!id || !status) {
    return res.status(400).json({ success: false, message: 'Favor de verificar los datos de la consulta.' })
  }

  const rStatus = statusKeyPrefix({ key: status })
  redis.sRem(rStatus, id)
  res.json({ success: true, message: "Estatus eliminado con exito." })
})


apiRouter.get('/chat-sessions/:email', async (req: Request, res: Response) => {
  const { email } = req.params
  const rKey = chatSessionKeyPrefix({ key: email })
  console.log('rKey:', rKey)
  const data = await redis.hVals(rKey)
  console.log('data:', data)
  const chatSessions = data.map((json) => JSON.parse(json))
  res.json(chatSessions)
})

apiRouter.post('/chat-sessions', async (req: Request, res: Response) => {
  const { key, field, chatSession } = req.body
  if (!key || !field || !chatSession) {
    return res.status(400).json({ success: false, message: 'Favor de verificar los datos de la consulta.' })
  }

  const rKey = chatSessionKeyPrefix({ key })
  const exists = await redis.hExists(rKey, field)
  if (exists) {
    return res.status(409).json({ success: false, message: 'La sesion de chat ya existe' })
  }
  await redis.hSet(rKey, field, chatSession)
  res.status(201).json({ success: true, message: "Sesion de chat creada con exito." })
})

apiRouter.post('/chat-messages/:chatId', async (req: Request, res: Response) => {
  const { chatId } = req.params;
  const message = req.body;
  if (!message || !message.uuid || !message.text || !message.username || !message.date) {
    return res.status(400).json({ error: 'Invalid message payload' });
  }
  try {
    await redis.rPush(chatMessagesKey({ key: chatId }), JSON.stringify(message));
    res.status(201).json({ success: true });
  } catch (err: any) {
    res.status(500).json({ error: err.message });
  }
});

const data = [
  {
    uuid: '1',
    text: 'Hola, ¿cómo estás?',
    username: 'm.snowman@gmail.com',
    date: new Date().getMilliseconds(),
    type: 'text',
  },
  {
    uuid: '2',
    text: 'Hola que tal...',
    username: 'luis.enrique@sadbh.com',
    date: new Date().getMilliseconds(),
    type: 'text',
  },
  {
    uuid: '3',
    text: 'Todo bien, gracias. ¿En qué puedo ayudarte?',
    username: 'luis.enrique@sadbh.com',
    date: new Date().getMilliseconds(),
    type: 'text',
  },
  {
    uuid: '4',
    text: 'Necesito revisar algunos documentos legales',
    username: 'm.snowman@gmail.com',
    date: new Date().getMilliseconds(),
    type: 'text',
  },
  {
    uuid: '5',
    text: 'Perfecto, puedo ayudarte con eso',
    username: 'luis.enrique@sadbh.com',
    date: new Date().getMilliseconds(),
    type: 'text',
  }
]

function loadData() {
  console.log('loading data:', data)
  for (const d of data) {
    redis.rPush(chatMessagesKey({ key: 'f61af82c-8895-4a59-864e-4a6e56a2d6b5' }), JSON.stringify(d));
  }
}

apiRouter.get('/chat-messages/:chatId', async (req: Request, res: Response) => {
  const { chatId } = req.params;
  try {
    //loadData()
    const key = chatMessagesKey({ key: chatId })
    console.log('key:', key)
    const rawMessages = await redis.lRange(key, 0, -1);
    const messages = rawMessages.map(msg => JSON.parse(msg));
    console.log('messages:', messages)
    res.json(messages);
  } catch (err: any) {
    res.status(500).json({ error: err.message });
  }
});
// Ping
apiRouter.get('/health', async (_req: Request, res: Response) => {
  const result = await redis.ping();
  res.json({ success: true, message: result });
});

const allowedOrigins = [
  'https://5e63-2806-2f0-9f80-eebe-edd4-2f04-3d01-133f.ngrok-free.app',
  'https://561b65885c20.ngrok-free.app',
  'https://e0b27e45c80f.ngrok-free.app',
  'http://localhost:5173'
]

// middleware CORS personalizado
const apiCors = cors({
  origin: (origin, callback) => {
    if (!origin || allowedOrigins.includes(origin)) {
      callback(null, true);
    } else {
      callback(new Error(`Origin ${origin} not allowed by CORS`));
    }
  },
  methods: ['GET', 'POST', 'DELETE', 'PUT'],
  allowedHeaders: [
    'Content-Type',
    'ngrok-skip-browser-warning'],
  credentials: true,
});

//app.options('/api/*', apiCors)
app.use(apiCors)
app.use(express.json())
app.use('/api', apiRouter)

const PORT = process.env.PORT || 3030;
app.listen(PORT, () => {
  console.log(`🚀 API server running at http://localhost:${PORT}`);
});
