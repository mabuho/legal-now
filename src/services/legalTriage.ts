import { ref } from 'vue'

export interface TriageResponse {
  message: string
  intent: 'question' | 'classification' | 'referral' | 'information'
  suggestedAction?: {
    type: 'consult_lawyer' | 'document_review' | 'legal_information' | 'emergency'
    specialization?: string
    urgency: 'low' | 'medium' | 'high'
  }
  nextSteps?: string[]
}

export interface ChatMessage {
  id: string
  content: string
  sender: 'user' | 'bot'
  timestamp: string
  response?: TriageResponse
}

class LegalTriageService {
  private messages = ref<ChatMessage[]>([])

  constructor() {
    // Initialize with welcome message
    this.messages.value = [{
      id: '1',
      content: '¡Hola! Soy tu asistente legal virtual. ¿En qué puedo ayudarte hoy?',
      sender: 'bot',
      timestamp: new Date().toISOString(),
      response: {
        message: '¡Hola! Soy tu asistente legal virtual. ¿En qué puedo ayudarte hoy?',
        intent: 'information',
        nextSteps: [
          'Describe tu situación legal',
          'Especifica el área de derecho relacionada',
          'Menciona si es un caso urgente'
        ]
      }
    }]
  }

  getMessages() {
    return this.messages
  }

  async sendMessage(content: string): Promise<ChatMessage> {
    // Add user message
    const userMessage: ChatMessage = {
      id: Date.now().toString(),
      content,
      sender: 'user',
      timestamp: new Date().toISOString()
    }
    this.messages.value.push(userMessage)

    // Process message with AI triage logic
    const response = await this.processMessage(content)
    
    // Add bot response
    const botMessage: ChatMessage = {
      id: (Date.now() + 1).toString(),
      content: response.message,
      sender: 'bot',
      timestamp: new Date().toISOString(),
      response
    }
    this.messages.value.push(botMessage)

    return botMessage
  }

  private async processMessage(content: string): Promise<TriageResponse> {
    // Mock AI processing - In production, this would call an actual AI service
    await new Promise(resolve => setTimeout(resolve, 1000)) // Simulate API call

    // Simple keyword-based classification for demo
    const keywords = {
      laboral: ['despido', 'contrato', 'trabajo', 'salario', 'empleador'],
      familiar: ['divorcio', 'custodia', 'alimentos', 'matrimonio', 'pension', 'seguro', 'prestaciones'],
      penal: ['denuncia', 'delito', 'robo', 'agresión'],
      civil: ['contrato', 'arrendamiento', 'propiedad', 'deuda']
    }

    let detectedArea = 'general'
    let urgency: 'low' | 'medium' | 'high' = 'low'

    // Simple keyword matching
    for (const [area, words] of Object.entries(keywords)) {
      if (words.some(word => content.toLowerCase().includes(word))) {
        detectedArea = area
        break
      }
    }

    // Check urgency
    if (content.toLowerCase().includes('urgente') || content.toLowerCase().includes('emergencia')) {
      urgency = 'high'
    }

    return {
      message: this.generateResponse(detectedArea, urgency),
      intent: 'classification',
      suggestedAction: {
        type: 'consult_lawyer',
        specialization: detectedArea,
        urgency
      },
      nextSteps: [
        'Revisar abogados disponibles en el marketplace',
        'Agendar una consulta inicial',
        'Preparar documentación relevante'
      ]
    }
  }

  private generateResponse(area: string, urgency: 'low' | 'medium' | 'high'): string {
    const responses = {
      laboral: 'Entiendo que tienes una situación laboral. Basado en tu descripción, te recomiendo consultar con un especialista en derecho laboral.',
      familiar: 'Tu caso parece estar relacionado con derecho familiar. Un abogado especializado podrá orientarte mejor sobre tus opciones.',
      penal: 'He identificado que tu caso es de naturaleza penal. Es importante que recibas asesoría legal especializada lo antes posible.',
      civil: 'Tu consulta está relacionada con derecho civil. Te puedo ayudar a conectar con un abogado especializado en esta área.',
      general: 'Gracias por compartir tu situación. Para poder ayudarte mejor, ¿podrías proporcionar más detalles sobre tu caso?'
    }

    const urgencyMsg = urgency === 'high' 
      ? ' Dado que es un caso urgente, te sugiero agendar una consulta inmediata con un abogado.'
      : ''

    return responses[area as keyof typeof responses] + urgencyMsg
  }
}

export const legalTriageService = new LegalTriageService() 