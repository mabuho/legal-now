<template>
  <div class="bg-surface-base min-h-screen font-body">
    <!-- NAV -->
    <header class="sticky top-0 z-20 bg-surface-base/80 backdrop-blur-md border-b border-border-subtle">
      <div class="max-w-7xl mx-auto px-6 h-16 flex items-center justify-between">
        <span class="font-heading font-extrabold text-xl bg-gradient-to-r from-brand-primary to-indigo-400 bg-clip-text text-transparent select-none">
          LegalNow
        </span>
        <nav class="hidden md:flex items-center gap-8">
          <a href="#como-funciona" class="font-body text-sm text-text-muted hover:text-text-primary transition-colors">Abogados</a>
          <a href="#como-funciona" class="font-body text-sm text-text-muted hover:text-text-primary transition-colors">Cómo funciona</a>
        </nav>
        <router-link
          to="/login"
          class="font-body text-sm text-text-primary border border-border-default px-4 py-2 rounded-btn hover:border-brand-primary/40 transition-colors"
        >
          Iniciar sesión
        </router-link>
      </div>
    </header>

    <!-- HERO -->
    <section class="bg-gradient-to-b from-[#060d1a] via-surface-base to-surface-base">
      <div class="max-w-7xl mx-auto px-6 pt-28 pb-24 flex flex-col items-center text-center">
        <div class="inline-flex items-center gap-2 px-4 py-2 rounded-tag bg-brand-primary/[0.08] border border-brand-primary/20 text-brand-primary text-sm font-body mb-8">
          <span class="w-2 h-2 rounded-full bg-brand-primary animate-pulse"></span>
          Abogados verificados · disponibles ahora
        </div>

        <h1 class="font-heading font-extrabold text-5xl md:text-6xl tracking-tight text-text-primary max-w-3xl">
          Asesoría legal
          <span class="bg-gradient-to-r from-brand-primary to-indigo-400 bg-clip-text text-transparent">en minutos</span>,
          no en semanas.
        </h1>

        <p class="font-body text-text-secondary text-lg md:text-xl mt-6 max-w-xl">
          Conectamos personas con abogados certificados para resolver dudas legales de forma rápida, segura y accesible.
        </p>

        <router-link
          to="/login"
          class="mt-10 inline-flex items-center gap-2 px-8 py-4 rounded-btn bg-gradient-to-r from-brand-primary-dark to-brand-accent text-white font-body font-semibold shadow-glow-btn hover:opacity-90 transition-opacity text-base"
        >
          Consultar ahora →
        </router-link>

        <div class="mt-16 flex flex-col sm:flex-row items-center gap-8 sm:gap-0 sm:divide-x sm:divide-border-subtle">
          <div class="sm:px-10 flex flex-col items-center">
            <span class="font-heading text-brand-primary font-bold text-2xl">+500</span>
            <span class="font-body text-text-muted text-xs mt-1">Abogados</span>
          </div>
          <div class="sm:px-10 flex flex-col items-center">
            <span class="font-heading text-brand-primary font-bold text-2xl">4.9★</span>
            <span class="font-body text-text-muted text-xs mt-1">Calificación</span>
          </div>
          <div class="sm:px-10 flex flex-col items-center">
            <span class="font-heading text-brand-primary font-bold text-2xl">&lt;5min</span>
            <span class="font-body text-text-muted text-xs mt-1">Respuesta</span>
          </div>
        </div>
      </div>
    </section>

    <!-- CÓMO FUNCIONA -->
    <section id="como-funciona" class="max-w-7xl mx-auto px-6 py-24">
      <h2 class="font-heading font-bold text-3xl md:text-4xl text-text-primary text-center tracking-tight">
        Cómo funciona
      </h2>
      <p class="font-body text-text-secondary text-center mt-4 max-w-xl mx-auto">
        Tres pasos para resolver tu situación legal desde hoy.
      </p>

      <div class="grid md:grid-cols-3 gap-8 mt-16">
        <div v-for="step in steps" :key="step.number" class="bg-surface-card border border-border-default rounded-card p-8 flex flex-col gap-4">
          <span class="font-heading font-bold text-brand-primary text-xs tracking-widest">{{ step.number }}</span>
          <h3 class="font-heading font-semibold text-text-primary text-lg">{{ step.title }}</h3>
          <p class="font-body text-text-secondary text-sm">{{ step.description }}</p>
        </div>
      </div>
    </section>

    <!-- ESPECIALIDADES -->
    <section class="max-w-7xl mx-auto px-6 py-24">
      <h2 class="font-heading font-bold text-3xl md:text-4xl text-text-primary text-center tracking-tight">
        Especialidades
      </h2>
      <p class="font-body text-text-secondary text-center mt-4 max-w-xl mx-auto">
        Cobertura legal en las áreas que más necesitas.
      </p>

      <div class="grid sm:grid-cols-2 lg:grid-cols-3 gap-8 mt-16">
        <div v-for="specialty in specialties" :key="specialty.name" class="bg-surface-card border border-border-default rounded-card p-8 flex flex-col gap-3">
          <div class="w-12 h-12 rounded-full flex items-center justify-center bg-brand-primary/10">
            <component :is="specialty.icon" class="w-6 h-6 text-brand-primary" />
          </div>
          <h3 class="font-heading font-semibold text-text-primary text-lg">{{ specialty.name }}</h3>
          <p class="font-body text-text-secondary text-sm">{{ specialty.description }}</p>
        </div>
      </div>
    </section>

    <!-- FAQ -->
    <section id="faq" class="max-w-4xl mx-auto px-6 py-24">
      <h2 class="font-heading font-bold text-3xl md:text-4xl text-text-primary text-center tracking-tight">
        Preguntas frecuentes
      </h2>
      <p class="font-body text-text-secondary text-center mt-4 max-w-xl mx-auto">
        Resolvemos tus dudas sobre nuestra plataforma.
      </p>

      <div class="mt-16 space-y-4">
        <div
          v-for="(faq, i) in faqs"
          :key="i"
          class="bg-surface-card border border-border-default rounded-card overflow-hidden"
        >
          <button
            @click="toggleFaq(i)"
            class="w-full flex items-center justify-between px-6 py-5 text-left font-body font-semibold text-text-primary hover:bg-surface-raised transition-colors"
            :aria-expanded="openFaq === i"
          >
            {{ faq.question }}
            <svg
              class="w-5 h-5 text-brand-primary transition-transform duration-200 flex-shrink-0 ml-4"
              :class="openFaq === i ? 'rotate-180' : ''"
              fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"
            >
              <path stroke-linecap="round" stroke-linejoin="round" d="m6 9 6 6 6-6" />
            </svg>
          </button>
          <transition
            enter-active-class="transition-all duration-200 ease-out"
            enter-from-class="opacity-0 max-h-0"
            enter-to-class="opacity-100 max-h-40"
            leave-active-class="transition-all duration-150 ease-in"
            leave-from-class="opacity-100 max-h-40"
            leave-to-class="opacity-0 max-h-0"
          >
            <div v-if="openFaq === i" class="px-6 pb-5 font-body text-text-secondary text-sm leading-relaxed">
              {{ faq.answer }}
            </div>
          </transition>
        </div>
      </div>
    </section>

    <!-- FOOTER -->
    <footer class="bg-surface-raised border-t border-border-subtle">
      <div class="max-w-7xl mx-auto px-6 py-16 grid grid-cols-2 md:grid-cols-4 gap-10">
        <div>
          <p class="font-body text-xs font-bold text-text-muted uppercase tracking-widest mb-4">Producto</p>
          <ul class="space-y-3">
            <li><a href="#como-funciona" class="font-body text-sm text-text-secondary hover:text-text-primary transition-colors">Cómo funciona</a></li>
            <li><a href="#" class="font-body text-sm text-text-secondary hover:text-text-primary transition-colors">Abogados</a></li>
            <li><a href="#" class="font-body text-sm text-text-secondary hover:text-text-primary transition-colors">Especialidades</a></li>
          </ul>
        </div>
        <div>
          <p class="font-body text-xs font-bold text-text-muted uppercase tracking-widest mb-4">Abogados</p>
          <ul class="space-y-3">
            <li><a href="#" class="font-body text-sm text-text-secondary hover:text-text-primary transition-colors">Unirse</a></li>
            <li><a href="#" class="font-body text-sm text-text-secondary hover:text-text-primary transition-colors">Requisitos</a></li>
            <li><a href="#" class="font-body text-sm text-text-secondary hover:text-text-primary transition-colors">Pagos</a></li>
          </ul>
        </div>
        <div>
          <p class="font-body text-xs font-bold text-text-muted uppercase tracking-widest mb-4">Legal</p>
          <ul class="space-y-3">
            <li><a href="#" class="font-body text-sm text-text-secondary hover:text-text-primary transition-colors">Privacidad</a></li>
            <li><a href="#" class="font-body text-sm text-text-secondary hover:text-text-primary transition-colors">Términos</a></li>
            <li><a href="#" class="font-body text-sm text-text-secondary hover:text-text-primary transition-colors">Contacto</a></li>
          </ul>
        </div>
        <div>
          <p class="font-body text-xs font-bold text-text-muted uppercase tracking-widest mb-4">RRSS</p>
          <ul class="space-y-3">
            <li><a href="#" class="font-body text-sm text-text-secondary hover:text-text-primary transition-colors">Twitter / X</a></li>
            <li><a href="#" class="font-body text-sm text-text-secondary hover:text-text-primary transition-colors">LinkedIn</a></li>
            <li><a href="#" class="font-body text-sm text-text-secondary hover:text-text-primary transition-colors">Instagram</a></li>
          </ul>
        </div>
      </div>
      <div class="border-t border-border-subtle">
        <div class="max-w-7xl mx-auto px-6 py-6">
          <p class="font-body text-text-muted text-xs text-center">
            © 2026 LegalNow. Todos los derechos reservados.
          </p>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup lang="ts">
import { ref, markRaw } from 'vue'
import {
  UsersIcon,
  BriefcaseIcon,
  HomeIcon,
  ExclamationTriangleIcon,
  BuildingOfficeIcon,
} from '@heroicons/vue/24/outline'

const openFaq = ref<number | null>(null)
const toggleFaq = (i: number) => {
  openFaq.value = openFaq.value === i ? null : i
}

const steps = [
  {
    number: '01',
    title: 'Describe tu caso',
    description: 'Cuéntanos brevemente tu situación legal en lenguaje sencillo, sin tecnicismos.',
  },
  {
    number: '02',
    title: 'Match con abogado',
    description: 'Nuestro sistema te conecta en segundos con el abogado más adecuado para tu caso.',
  },
  {
    number: '03',
    title: 'Resuelve tu problema',
    description: 'Recibe asesoría personalizada por chat o videollamada y avanza con tu solución.',
  },
]

const specialties = [
  {
    name: 'Familia',
    description: 'Custodia, divorcio, pensión alimenticia y acuerdos familiares para proteger a los tuyos.',
    icon: markRaw(UsersIcon),
  },
  {
    name: 'Laboral',
    description: 'Derechos laborales, contratos, despidos y conflictos con empleadores.',
    icon: markRaw(BriefcaseIcon),
  },
  {
    name: 'Renta',
    description: 'Contratos de arrendamiento, derechos del inquilino, desalojos y obligaciones del arrendador.',
    icon: markRaw(HomeIcon),
  },
  {
    name: 'Fraude',
    description: 'Estafas, robo de identidad y contratos fraudulentos para defender tus intereses.',
    icon: markRaw(ExclamationTriangleIcon),
  },
  {
    name: 'Empresarial',
    description: 'Constitución de empresas, contratos comerciales, cumplimiento normativo y litigios.',
    icon: markRaw(BuildingOfficeIcon),
  },
]

const faqs = [
  {
    question: '¿Qué tan rápido puedo obtener asesoría legal?',
    answer:
      'Generalmente respondemos en menos de 5 minutos, disponible las 24 horas del día. Nuestro sistema te conecta de forma inmediata con un abogado calificado.',
  },
  {
    question: '¿Los abogados están certificados y verificados?',
    answer:
      'Sí, todos los abogados en LegalNow pasan por un proceso riguroso de verificación de cédula profesional y credenciales antes de unirse a la plataforma.',
  },
  {
    question: '¿Cómo se protege mi privacidad?',
    answer:
      'Utilizamos cifrado de nivel bancario para garantizar que todas las conversaciones y datos permanezcan completamente confidenciales y seguros.',
  },
  {
    question: '¿Qué tipos de problemas legales puedo consultar?',
    answer:
      'Cubrimos una amplia gama de áreas: familia, laboral, arrendamiento, fraude, empresarial y más. Escríbenos tu caso y te orientamos.',
  },
]
</script>
