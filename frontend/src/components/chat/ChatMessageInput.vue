<template>
  <form @submit.prevent="send" ref="formRef"
    class="bg-slate-800 border border-slate-700 rounded-2xl p-2 flex items-center gap-2 shadow relative">
    <!-- Input wrapper with clip button inside -->
    <div class="relative flex-1 max-w-[calc(100%-4rem)]">
      <input v-model="message" type="text" placeholder="Escribe un mensaje..."
        class="w-full bg-slate-900 text-white rounded-lg pr-12 py-2 md:py-3 text-sm md:text-base focus:outline-none focus:ring-2 focus:ring-blue-500 border border-slate-700" />
      <!-- Clip Icon Button inside input (right side) -->
      <button type="button" ref="clipBtnRef" @click="showModal = !showModal"
        class="absolute right-2 top-1/2 -translate-y-1/2 flex items-center justify-center p-2 rounded-lg hover:bg-slate-700 focus:outline-none focus:ring-2 focus:ring-blue-500 z-10">
        <svg xmlns="http://www.w3.org/2000/svg" class="w-5 h-5 text-slate-400" fill="none" viewBox="0 0 24 24"
          stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
            d="M15.172 7l-6.586 6.586a2 2 0 102.828 2.828l7.07-7.07a4 4 0 00-5.656-5.657l-7.07 7.07a6 6 0 108.485 8.485l6.364-6.364" />
        </svg>
      </button>
    </div>
    <!-- Send Icon Button outside input -->
    <button type="submit"
      class="flex items-center justify-center bg-blue-600 hover:bg-blue-700 text-white p-2 md:p-3 rounded-lg transition-colors shadow">
      <svg xmlns="http://www.w3.org/2000/svg" class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 12h14M12 5l7 7-7 7" />
      </svg>
    </button>

    <!-- Hidden file inputs -->
    <input ref="galleryInputRef" type="file" accept=".png,.jpg,.jpeg" multiple @change="handleGalleryFiles"
      class="hidden" />
    <input ref="documentInputRef" type="file" accept=".doc,.pdf" multiple @change="handleDocumentFiles"
      class="hidden" />

    <!-- Centered Modal for file type selection using BaseModal -->
    <BaseModal v-if="showModal" :visible="showModal" @close="showModal = false"
      class="fixed inset-0 z-50 flex items-center justify-center bg-black/40">
      <template #header>
        <h3 class="text-lg font-bold mb-4 text-center">Selecciona el tipo de archivo</h3>
      </template>
      <div class="grid grid-cols-3 gap-4">
        <AttachmentOption v-for="option in attachmentOptions" :key="option.icon" :icon="option.icon"
          :label="option.label" :color="option.color" @click="handleAttachmentOptionClick(option.icon)" />
      </div>
    </BaseModal>

    <!-- Camera Modal -->
    <transition name="fade">
      <div v-if="showCameraModal" class="fixed inset-0 z-50 bg-black">
        <!-- Header -->
        <div class="absolute top-0 left-0 right-0 z-10 bg-black/50 backdrop-blur-sm p-4">
          <div class="flex items-center justify-between">
            <h3 class="text-lg font-bold text-white">Cámara</h3>
            <button @click="closeCameraModal" class="text-white hover:text-gray-300 text-2xl">
              &times;
            </button>
          </div>
        </div>

        <!-- Camera preview -->
        <div class="relative w-full h-full">
          <video ref="cameraVideoRef" class="w-full h-full object-cover" autoplay muted playsinline></video>

          <!-- Captured image preview overlay -->
          <div v-if="capturedImage" class="absolute inset-0 bg-black">
            <img :src="capturedImage" class="w-full h-full object-cover" alt="Captured photo" />
          </div>
        </div>

        <!-- Camera controls overlay -->
        <div class="absolute bottom-0 left-0 right-0 bg-black/50 backdrop-blur-sm p-6">
          <!-- Camera status -->
          <div class="text-center text-sm text-white mb-4">
            <p v-if="cameraError">{{ cameraError }}</p>
            <p v-else-if="!cameraStream">Iniciando cámara...</p>
            <p v-else-if="!capturedImage">Toca "Tomar Foto" para capturar</p>
            <p v-else>Revisa la foto y decide si usarla o retomarla</p>
          </div>

          <!-- Camera controls -->
          <div class="flex justify-center items-center gap-4 mb-4">
            <!-- Retake button (only shown when photo is captured) -->
            <button v-if="capturedImage" @click="retakePhoto"
              class="flex items-center gap-2 px-6 py-3 bg-red-600 hover:bg-red-700 text-white rounded-lg transition-colors">
              <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                  d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
              </svg>
              <span>Retomar</span>
            </button>

            <!-- Capture button (only shown when not captured) -->
            <button v-if="!capturedImage" @click="capturePhoto"
              class="flex items-center gap-2 px-8 py-4 bg-blue-600 hover:bg-blue-700 text-white rounded-lg transition-colors text-lg font-medium">
              <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                  d="M3 9a2 2 0 012-2h.93a2 2 0 001.664-.89l.812-1.22A2 2 0 0110.07 4h3.86a2 2 0 011.664.89l.812 1.22A2 2 0 0018.07 7H19a2 2 0 012 2v9a2 2 0 01-2 2H5a2 2 0 01-2-2V9z" />
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                  d="M15 13a3 3 0 11-6 0 3 3 0 016 0z" />
              </svg>
              <span>Tomar Foto</span>
            </button>

            <!-- Use photo button (only shown when photo is captured) -->
            <button v-if="capturedImage" @click="useCapturedPhoto"
              class="flex items-center gap-2 px-6 py-3 bg-green-600 hover:bg-green-700 text-white rounded-lg transition-colors">
              <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
              </svg>
              <span>Usar Foto</span>
            </button>
          </div>

          <!-- Close camera button -->
          <div class="text-center">
            <button @click="closeCameraModal"
              class="px-6 py-2 text-white hover:text-gray-300 hover:bg-white/10 rounded-lg transition-colors">
              Cerrar Cámara
            </button>
          </div>
        </div>
      </div>
    </transition>

    <!-- File Preview Modal -->
    <BaseModal v-if="showPreviewModal" :visible="showPreviewModal" @close="closePreviewModal">
      <template #header>
        <h3 class="text-lg font-bold mb-4 text-center">Vista previa de archivos</h3>
      </template>
      <div class="space-y-4">
        <!-- Caption input -->
        <div>
          <label class="block text-sm font-medium text-slate-300 mb-2">Mensaje (opcional)</label>
          <textarea v-model="fileCaption" placeholder="Agrega un mensaje a tus archivos..."
            class="w-full bg-slate-800 border border-slate-700 rounded-lg p-3 text-white placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-blue-500 resize-none"
            rows="3"></textarea>
        </div>

        <!-- File previews -->
        <div class="space-y-3">
          <div v-for="(file, index) in selectedFiles" :key="index"
            class="flex items-center gap-3 p-3 bg-slate-800 rounded-lg border border-slate-700">
            <!-- File preview -->
            <div class="flex-shrink-0">
              <div v-if="file.type.startsWith('image/')" class="w-16 h-16 rounded-lg overflow-hidden bg-slate-700">
                <img :src="file.preview" :alt="file.name" class="w-full h-full object-cover" />
              </div>
              <div v-else class="w-16 h-16 rounded-lg bg-slate-700 flex items-center justify-center">
                <svg class="w-8 h-8 text-slate-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                    d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                </svg>
              </div>
            </div>

            <!-- File info -->
            <div class="flex-1 min-w-0">
              <p class="text-sm font-medium text-slate-200 truncate">{{ file.name }}</p>
              <p class="text-xs text-slate-400">{{ formatFileSize(file.size) }}</p>
            </div>

            <!-- Delete button -->
            <button @click="removeFile(index)"
              class="flex-shrink-0 p-2 text-slate-400 hover:text-red-400 hover:bg-red-400/10 rounded-lg transition-colors">
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                  d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
              </svg>
            </button>
          </div>
        </div>

        <!-- Add more files button -->
        <button @click="showModal = true; showPreviewModal = false"
          class="w-full p-3 border-2 border-dashed border-slate-600 rounded-lg text-slate-400 hover:text-slate-300 hover:border-slate-500 transition-colors">
          <div class="flex items-center justify-center gap-2">
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
            </svg>
            <span>Agregar más archivos</span>
          </div>
        </button>

        <!-- Send button -->
        <button @click="sendFiles" :disabled="selectedFiles.length === 0"
          class="w-full bg-blue-600 hover:bg-blue-700 disabled:bg-slate-600 disabled:cursor-not-allowed text-white py-3 px-4 rounded-lg font-medium transition-colors">
          Enviar {{ selectedFiles.length }} archivo{{ selectedFiles.length !== 1 ? 's' : '' }}
        </button>
      </div>
    </BaseModal>
  </form>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, computed, nextTick, watch, defineComponent, h } from 'vue'
import BaseModal from '@/components/BaseModal.vue'

interface SelectedFile {
  file: File
  name: string
  size: number
  type: string
  preview?: string
}

const message = ref('')
const showModal = ref(false)
const showPreviewModal = ref(false)
const showCameraModal = ref(false)
const fileCaption = ref('')
const selectedFiles = ref<SelectedFile[]>([])
const emit = defineEmits(['sendMessage', 'sendFiles'])

// Camera state
const cameraVideoRef = ref<HTMLVideoElement | null>(null)
const cameraStream = ref<MediaStream | null>(null)
const capturedImage = ref<string | null>(null)
const cameraError = ref<string | null>(null)

const isMobile = computed(() => window.innerWidth < 768)
const formRef = ref<HTMLElement | null>(null)
const clipBtnRef = ref<HTMLElement | null>(null)
const galleryInputRef = ref<HTMLInputElement | null>(null)
const documentInputRef = ref<HTMLInputElement | null>(null)
const bubblePosition = ref<{ left: number; top: number } | null>(null)

function send() {
  if (!message.value.trim()) return
  emit('sendMessage', message.value.trim())
  message.value = ''
}

function sendFiles() {
  if (selectedFiles.value.length === 0) return

  const filesData = {
    files: selectedFiles.value.map(f => f.file),
    caption: fileCaption.value.trim()
  }

  emit('sendFiles', filesData)
  closePreviewModal()
}

function closePreviewModal() {
  showPreviewModal.value = false
  selectedFiles.value = []
  fileCaption.value = ''
}

function closeCameraModal() {
  showCameraModal.value = false
  stopCamera()
  capturedImage.value = null
  cameraError.value = null
}

function formatFileSize(bytes: number): string {
  if (bytes === 0) return '0 Bytes'
  const k = 1024
  const sizes = ['Bytes', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

function createFilePreview(file: File): Promise<string> {
  return new Promise((resolve) => {
    if (file.type.startsWith('image/')) {
      const reader = new FileReader()
      reader.onload = (e) => resolve(e.target?.result as string)
      reader.readAsDataURL(file)
    } else {
      resolve('')
    }
  })
}

async function addFiles(files: FileList) {
  for (const file of Array.from(files)) {
    const preview = await createFilePreview(file)
    selectedFiles.value.push({
      file,
      name: file.name,
      size: file.size,
      type: file.type,
      preview
    })
  }

  if (selectedFiles.value.length > 0) {
    showPreviewModal.value = true
    showModal.value = false
  }
}

function removeFile(index: number) {
  selectedFiles.value.splice(index, 1)
  if (selectedFiles.value.length === 0) {
    closePreviewModal()
  }
}

async function handleGalleryFiles(event: Event) {
  const target = event.target as HTMLInputElement
  if (target.files) {
    await addFiles(target.files)
  }
  target.value = '' // Reset input
}

async function handleDocumentFiles(event: Event) {
  const target = event.target as HTMLInputElement
  if (target.files) {
    await addFiles(target.files)
  }
  target.value = '' // Reset input
}

async function startCamera() {
  try {
    cameraError.value = null
    const stream = await navigator.mediaDevices.getUserMedia({
      video: {
        facingMode: 'environment',
        width: { ideal: 1920 },
        height: { ideal: 1080 }
      }
    })

    cameraStream.value = stream

    if (cameraVideoRef.value) {
      cameraVideoRef.value.srcObject = stream
      await cameraVideoRef.value.play()
    }
  } catch (error) {
    console.error('Error accessing camera:', error)
    cameraError.value = 'No se pudo acceder a la cámara. Por favor, verifica los permisos.'
  }
}

function stopCamera() {
  if (cameraStream.value) {
    cameraStream.value.getTracks().forEach(track => track.stop())
    cameraStream.value = null
  }
}

function capturePhoto() {
  if (!cameraVideoRef.value) return

  const canvas = document.createElement('canvas')
  canvas.width = cameraVideoRef.value.videoWidth
  canvas.height = cameraVideoRef.value.videoHeight
  const ctx = canvas.getContext('2d')

  if (ctx) {
    ctx.drawImage(cameraVideoRef.value, 0, 0)
    capturedImage.value = canvas.toDataURL('image/jpeg', 0.8)
  }
}

function retakePhoto() {
  capturedImage.value = null
}

async function useCapturedPhoto() {
  if (!capturedImage.value) return

  try {
    // Convert data URL to blob
    const response = await fetch(capturedImage.value)
    const blob = await response.blob()

    const file = new File([blob], `camera_${Date.now()}.jpg`, { type: 'image/jpeg' })

    // Create a DataTransfer object to simulate FileList
    const dataTransfer = new DataTransfer()
    dataTransfer.items.add(file)
    await addFiles(dataTransfer.files)

    // Reset for next photo instead of closing modal
    capturedImage.value = null

    // Show success message briefly
    const originalStatus = cameraError.value
    cameraError.value = 'Foto agregada. Puedes tomar otra foto o cerrar la cámara.'
    setTimeout(() => {
      if (cameraError.value === 'Foto agregada. Puedes tomar otra foto o cerrar la cámara.') {
        cameraError.value = originalStatus
      }
    }, 2000)

  } catch (error) {
    console.error('Error processing captured photo:', error)
    cameraError.value = 'Error al procesar la foto capturada.'
  }
}

async function handleCameraCapture() {
  showCameraModal.value = true
  showModal.value = false

  // Start camera after modal is shown
  await nextTick()
  await startCamera()
}

function handleAttachmentOptionClick(icon: string) {
  showModal.value = false

  switch (icon) {
    case 'gallery':
      galleryInputRef.value?.click()
      break
    case 'camera':
      handleCameraCapture()
      break
    case 'document':
      documentInputRef.value?.click()
      break
  }
}

function updateBubblePosition() {
  if (isMobile.value || !clipBtnRef.value || !formRef.value) return
  const btnRect = (clipBtnRef.value as HTMLElement).getBoundingClientRect()
  const formRect = (formRef.value as HTMLElement).getBoundingClientRect()
  bubblePosition.value = {
    left: btnRect.left - formRect.left,
    top: btnRect.top - formRect.top - 220 // height of modal + margin
  }
}

onMounted(() => {
  window.addEventListener('resize', updateBubblePosition)
  nextTick(updateBubblePosition)
})

onUnmounted(() => {
  window.removeEventListener('resize', updateBubblePosition)
  stopCamera()
})

watch(showModal, (val) => {
  if (val) nextTick(updateBubblePosition)
})

const attachmentOptions = [
  { icon: 'gallery', label: 'Galería', color: '#3B82F6' },
  { icon: 'camera', label: 'Cámara', color: '#EC4899' },
  { icon: 'document', label: 'Documento', color: '#8B5CF6' },
]

const icons: Record<string, string> = {
  gallery: `<svg class='w-7 h-7 mx-auto mb-1' fill='none' stroke='currentColor' stroke-width='2' viewBox='0 0 24 24'><rect x='3' y='3' width='18' height='18' rx='4' stroke='currentColor'/><path d='M8 17l3-3 2 2 3-4' stroke='currentColor'/></svg>`,
  camera: `<svg class='w-7 h-7 mx-auto mb-1' fill='none' stroke='currentColor' stroke-width='2' viewBox='0 0 24 24'><rect x='3' y='7' width='18' height='14' rx='4' stroke='currentColor'/><circle cx='12' cy='14' r='4' stroke='currentColor'/><path d='M8 7V5a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2' stroke='currentColor'/></svg>`,
  document: `<svg class='w-7 h-7 mx-auto mb-1' fill='none' stroke='currentColor' stroke-width='2' viewBox='0 0 24 24'><rect x='6' y='3' width='12' height='18' rx='2' stroke='currentColor'/><path d='M9 7h6M9 11h6M9 15h2' stroke='currentColor'/></svg>`,
}

const AttachmentOption = defineComponent({
  name: 'AttachmentOption',
  props: {
    icon: { type: String, required: true },
    label: { type: String, required: true },
    color: { type: String, required: true }
  },
  emits: ['click'],
  setup(props, { emit }) {
    return () => h(
      'button',
      {
        class: 'flex flex-col items-center justify-center rounded-xl bg-slate-800 hover:bg-slate-700 border border-slate-700 p-4 transition-colors',
        tabindex: 0,
        onClick: () => emit('click', props.icon)
      },
      [
        h('span', { innerHTML: icons[props.icon], style: { color: props.color } }),
        h('span', { class: 'text-xs mt-1', style: { color: '#b0b0b0' } }, props.label)
      ]
    )
  }
})
</script>
