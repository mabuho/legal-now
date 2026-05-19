<template>
  <!-- NAV -->
  <header class="sticky top-0 z-20 backdrop-blur bg-slate-950/70">
    <div class="max-w-7xl flex mr-auto ml-auto pt-4 pr-6 pb-4 pl-6 items-center justify-between">
      <a href="#" class="text-xl font-semibold tracking-tight select-none font-manrope">LN</a>
      <nav class="hidden md:flex items-center gap-8 text-sm font-medium">
        <a href="#how" class="transition-colors font-manrope hover:text-cyan-400">How it works</a>
        <a href="#benefits" class="transition-colors font-manrope hover:text-cyan-400">Benefits</a>
        <a href="#faq" class="transition-colors font-manrope hover:text-cyan-400">FAQ</a>
        <a href="#contact" class="transition-colors font-manrope hover:text-cyan-400">Contact</a>
      </nav>
      <a href="#cta"
        class="inline-flex items-center gap-2 rounded-md hover:bg-cyan-500 transition-colors px-4 py-2 text-sm font-semibold bg-cyan-600">
        <router-link to="/login" class="font-manrope">Login</router-link>
        <!-- <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none"
          stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"
          data-lucide="arrow-right" strokewidth="1.5" class="lucide lucide-arrow-right w-4 h-4">
          <path d="M5 12h14"></path>
          <path d="m12 5 7 7-7 7"></path>
        </svg> -->
      </a>
    </div>
    <div class="border-t border-slate-800"></div>
  </header>

  <!-- HERO (re-built) -->
  <section class="relative overflow-hidden bg-gradient-to-br from-indigo-950 via-slate-950 to-black">
    <div class="absolute -top-40 -left-40 w-96 h-96 rounded-full blur-3xl bg-cyan-600/20"></div>
    <div class="absolute -bottom-40 -right-40 w-96 h-96 rounded-full blur-3xl bg-indigo-600/20"></div>

    <div class="max-w-7xl mx-auto px-6 pt-32 pb-24 flex flex-col md:flex-row items-center gap-20">
      <!-- Copy -->
      <div class="flex-1 max-w-xl">
        <h1 class="text-4xl md:text-6xl font-medium tracking-tight font-montserrat">
          Get legal advice <span class="bg-gradient-to-r to-blue-500 bg-clip-text text-transparent from-cyan-400">in
            minutes</span>.
        </h1>
        <p class="text-lg md:text-2xl mt-6 font-montserrat font-medium text-slate-400">
          Fast, reliable answers from licensed attorneys—on your schedule.
        </p>
        <div class="mt-10 flex flex-col sm:flex-row gap-4">
          <a href="#cta"
            class="inline-flex items-center gap-2 rounded-md hover:bg-cyan-500 transition-colors px-6 py-3 text-base font-semibold font-manrope bg-cyan-600">
            Start your consultation
            <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none"
              stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"
              data-lucide="arrow-right" strokewidth="1.5" class="lucide lucide-arrow-right w-5 h-5">
              <path d="M5 12h14"></path>
              <path d="m12 5 7 7-7 7"></path>
            </svg>
          </a>
          <a href="#how"
            class="inline-flex items-center gap-2 rounded-md border hover:border-slate-500 transition-colors px-6 py-3 text-base font-semibold font-manrope border-slate-700">
            See how it works
            <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none"
              stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"
              data-lucide="play-circle" strokewidth="1.5" class="lucide lucide-play-circle w-5 h-5">
              <circle cx="12" cy="12" r="10"></circle>
              <polygon points="10 8 16 12 10 16 10 8"></polygon>
            </svg>
          </a>
        </div>
      </div>

      <!-- Mockup Card -->
      <div class="flex-1 w-full max-w-md relative">
        <!-- glow -->
        <div
          class="absolute inset-0 -translate-y-4 blur-2xl opacity-30 pointer-events-none bg-gradient-to-br from-cyan-500/70 rounded-3xl via-purple-600/70 to-indigo-600/70">
        </div>

        <div class="relative border rounded-3xl p-6 shadow-2xl bg-slate-900 border-slate-800">
          <!-- Header -->
          <div class="flex items-center gap-3 mb-6">
            <div class="w-9 h-9 rounded-full flex items-center justify-center text-xs font-semibold bg-cyan-600">LJ
            </div>
            <div>
              <p class="font-semibold font-manrope leading-none">Laura Jenkins</p>
              <p class="text-xs text-slate-400">Family Law • Online</p>
            </div>
            <span class="ml-auto w-2 h-2 rounded-full bg-green-500"></span>
          </div>

          <!-- Chat bubbles -->
          <div class="space-y-4 text-sm">
            <!-- user -->
            <div class="flex justify-end">
              <div
                class="max-w-[75%] bg-gradient-to-r px-4 py-3 rounded-2xl rounded-br-none font-manrope from-cyan-600 to-blue-600">
                Hi Laura, I need help with a custody agreement.
              </div>
            </div>
            <!-- lawyer -->
            <div class="flex">
              <div class="max-w-[75%] px-4 py-3 rounded-2xl rounded-bl-none font-manrope bg-slate-800 text-slate-200">
                Of course—can you share the current arrangement and any concerns?
              </div>
            </div>
            <!-- user -->
            <div class="flex justify-end">
              <div
                class="max-w-[75%] bg-gradient-to-r px-4 py-3 rounded-2xl rounded-br-none font-manrope from-cyan-600 to-blue-600">
                Sure, it's 50/50 but holidays are an issue...
              </div>
            </div>
          </div>

          <!-- Input -->
          <div class="mt-6 flex items-center gap-3">
            <input type="text" placeholder="Type a message…"
              class="flex-1 border rounded-xl px-4 py-2 text-sm placeholder-slate-500 focus:outline-none bg-slate-800/50 border-slate-700">
            <button class="p-3 rounded-xl hover:bg-cyan-500 transition-colors bg-cyan-600">
              <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none"
                stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" data-lucide="send"
                strokewidth="1.5" class="lucide lucide-send w-4 h-4 text-white">
                <path
                  d="M14.536 21.686a.5.5 0 0 0 .937-.024l6.5-19a.496.496 0 0 0-.635-.635l-19 6.5a.5.5 0 0 0-.024.937l7.93 3.18a2 2 0 0 1 1.112 1.11z">
                </path>
                <path d="m21.854 2.147-10.94 10.939"></path>
              </svg>
            </button>
          </div>
        </div>
      </div>
    </div>
  </section>

  <!-- HOW IT WORKS -->
  <section id="how" class="max-w-7xl mr-auto ml-auto pt-24 pr-6 pb-24 pl-6">
    <h2 class="text-3xl md:text-4xl font-semibold tracking-tight font-montserrat text-center">
      How it works
    </h2>
    <p class="mt-4 max-w-2xl mx-auto text-center font-medium text-slate-400">
      From question to qualified legal answer in three quick steps.
    </p>

    <div class="relative mt-16">
      <!-- Connecting line (desktop) -->
      <div class="hidden md:block absolute top-1/2 left-0 right-0 h-0.5 bg-slate-800"></div>

      <div class="grid md:grid-cols-3 gap-12">
        <!-- Step 1 -->
        <div class="relative flex flex-col items-center text-center">
          <span
            class="absolute -top-7 left-1/2 -translate-x-1/2 w-12 h-12 rounded-full border flex items-center justify-center text-lg font-semibold bg-zinc-950 border-slate-800">1</span>

          <div class="border rounded-2xl p-8 flex flex-col items-center bg-slate-900 border-slate-800">
            <div class="w-14 h-14 rounded-full flex items-center justify-center mb-6 bg-cyan-600/10">
              <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none"
                stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"
                data-lucide="message-circle" strokewidth="1.5"
                class="lucide lucide-message-circle w-7 h-7 text-cyan-400">
                <path d="M7.9 20A9 9 0 1 0 4 16.1L2 22Z"></path>
              </svg>
            </div>
            <h3 class="text-lg font-semibold tracking-tight font-manrope">Ask your question</h3>
            <p class="mt-2 text-sm text-slate-400">
              Describe your legal issue in plain language—no jargon needed.
            </p>
          </div>
        </div>

        <!-- Step 2 -->
        <div class="relative flex flex-col items-center text-center">
          <span
            class="absolute -top-7 left-1/2 -translate-x-1/2 w-12 h-12 rounded-full border flex items-center justify-center text-lg font-semibold bg-zinc-950 border-slate-800">2</span>

          <div class="border rounded-2xl p-8 flex flex-col items-center bg-slate-900 border-slate-800">
            <div class="w-14 h-14 rounded-full flex items-center justify-center mb-6 bg-cyan-600/10">
              <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none"
                stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"
                data-lucide="user-check" strokewidth="1.5" class="lucide lucide-user-check w-7 h-7 text-cyan-400">
                <path d="m16 11 2 2 4-4"></path>
                <path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2"></path>
                <circle cx="9" cy="7" r="4"></circle>
              </svg>
            </div>
            <h3 class="text-lg font-semibold tracking-tight font-manrope">We match you</h3>
            <p class="mt-2 text-sm text-slate-400">
              Instantly connect with the best-suited, licensed attorney for your case.
            </p>
          </div>
        </div>

        <!-- Step 3 -->
        <div class="relative flex flex-col items-center text-center">
          <span
            class="absolute -top-7 left-1/2 -translate-x-1/2 w-12 h-12 rounded-full border flex items-center justify-center text-lg font-semibold bg-zinc-950 border-slate-800">3</span>

          <div class="border rounded-2xl p-8 flex flex-col items-center bg-slate-900 border-slate-800">
            <div class="w-14 h-14 rounded-full flex items-center justify-center mb-6 bg-cyan-600/10">
              <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none"
                stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"
                data-lucide="file-text" strokewidth="1.5" class="lucide lucide-file-text w-7 h-7 text-cyan-400">
                <path d="M15 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V7Z"></path>
                <path d="M14 2v4a2 2 0 0 0 2 2h4"></path>
                <path d="M10 9H8"></path>
                <path d="M16 13H8"></path>
                <path d="M16 17H8"></path>
              </svg>
            </div>
            <h3 class="text-lg font-semibold tracking-tight font-manrope">Get answers &amp; docs</h3>
            <p class="mt-2 text-sm text-slate-400">
              Receive actionable advice—and any needed documents—within minutes.
            </p>
          </div>
        </div>
      </div>
    </div>
  </section>

  <!-- BENEFITS -->
  <section id="benefits" class="max-w-7xl mx-auto pt-24 px-6 pb-24">
    <h2 class="text-3xl md:text-4xl font-semibold tracking-tight font-montserrat text-center">
      Why people choose LegalNow
    </h2>
    <p class="mt-4 max-w-2xl mx-auto text-center font-medium text-slate-400">
      Legal guidance that’s fast, affordable, and always professional.
    </p>

    <div class="grid sm:grid-cols-2 lg:grid-cols-4 gap-8 mt-16">
      <!-- Benefit 1 -->
      <div class="border rounded-2xl p-8 bg-slate-900 border-slate-800">
        <div class="w-12 h-12 rounded-full flex items-center justify-center mb-6 bg-cyan-600/10">
          <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none"
            stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" data-lucide="clock"
            strokewidth="1.5" class="lucide lucide-clock w-6 h-6 text-cyan-400">
            <path d="M12 6v6l4 2"></path>
            <circle cx="12" cy="12" r="10"></circle>
          </svg>
        </div>
        <h3 class="text-lg font-semibold tracking-tight font-manrope">Lightning-fast</h3>
        <p class="mt-2 text-sm text-slate-400">
          Average response times under five minutes, any time of day.
        </p>
      </div>

      <!-- Benefit 2 -->
      <div class="border rounded-2xl p-8 bg-slate-900 border-slate-800">
        <div class="w-12 h-12 rounded-full flex items-center justify-center mb-6 bg-cyan-600/10">
          <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none"
            stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"
            data-lucide="badge-check" strokewidth="1.5" class="lucide lucide-badge-check w-6 h-6 text-cyan-400">
            <path
              d="M3.85 8.62a4 4 0 0 1 4.78-4.77 4 4 0 0 1 6.74 0 4 4 0 0 1 4.78 4.78 4 4 0 0 1 0 6.74 4 4 0 0 1-4.77 4.78 4 4 0 0 1-6.75 0 4 4 0 0 1-4.78-4.77 4 4 0 0 1 0-6.76Z">
            </path>
            <path d="m9 12 2 2 4-4"></path>
          </svg>
        </div>
        <h3 class="text-lg font-semibold tracking-tight font-manrope">Licensed experts</h3>
        <p class="mt-2 text-sm text-slate-400">
          Only qualified, vetted attorneys handle your questions.
        </p>
      </div>

      <!-- Benefit 3 -->
      <div class="border rounded-2xl p-8 bg-slate-900 border-slate-800">
        <div class="w-12 h-12 rounded-full flex items-center justify-center mb-6 bg-cyan-600/10">
          <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none"
            stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" data-lucide="tag"
            strokewidth="1.5" class="lucide lucide-tag w-6 h-6 text-cyan-400">
            <path
              d="M12.586 2.586A2 2 0 0 0 11.172 2H4a2 2 0 0 0-2 2v7.172a2 2 0 0 0 .586 1.414l8.704 8.704a2.426 2.426 0 0 0 3.42 0l6.58-6.58a2.426 2.426 0 0 0 0-3.42z">
            </path>
            <circle cx="7.5" cy="7.5" r=".5" fill="currentColor"></circle>
          </svg>
        </div>
        <h3 class="text-lg font-semibold tracking-tight font-manrope">Transparent pricing</h3>
        <p class="mt-2 text-sm text-slate-400">
          No hourly rates or surprise fees—just clear, upfront costs.
        </p>
      </div>

      <!-- Benefit 4 -->
      <div class="border rounded-2xl p-8 bg-slate-900 border-slate-800">
        <div class="w-12 h-12 rounded-full flex items-center justify-center mb-6 bg-cyan-600/10">
          <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none"
            stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"
            data-lucide="shield-check" strokewidth="1.5" class="lucide lucide-shield-check w-6 h-6 text-cyan-400">
            <path
              d="M20 13c0 5-3.5 7.5-7.66 8.95a1 1 0 0 1-.67-.01C7.5 20.5 4 18 4 13V6a1 1 0 0 1 1-1c2 0 4.5-1.2 6.24-2.72a1.17 1.17 0 0 1 1.52 0C14.51 3.81 17 5 19 5a1 1 0 0 1 1 1z">
            </path>
            <path d="m9 12 2 2 4-4"></path>
          </svg>
        </div>
        <h3 class="text-lg font-semibold tracking-tight font-manrope">Secure &amp; private</h3>
        <p class="mt-2 text-sm text-slate-400">
          Bank-level encryption keeps every conversation confidential.
        </p>
      </div>
    </div>
  </section>
  <section id="expertise" class="max-w-7xl mx-auto pt-24 px-6 pb-24">
    <h2 class="text-3xl md:text-4xl font-semibold tracking-tight font-montserrat text-center">
      Areas of Expertise
    </h2>
    <p class="mt-4 max-w-2xl mx-auto text-center font-medium text-slate-400">
      Explore the specialized legal guidance we offer across key areas to support your unique needs.
    </p>

    <div class="grid sm:grid-cols-2 lg:grid-cols-3 gap-10 mt-16">
      <!-- Family Law -->
      <div class="border rounded-2xl p-8 flex flex-col bg-slate-900 border-slate-800">
        <div class="w-14 h-14 rounded-full flex items-center justify-center mb-5 bg-cyan-600/10">
          <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none"
            stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" data-lucide="users"
            strokewidth="1.5" class="lucide lucide-users w-7 h-7 text-cyan-400">
            <path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2"></path>
            <path d="M16 3.128a4 4 0 0 1 0 7.744"></path>
            <path d="M22 21v-2a4 4 0 0 0-3-3.87"></path>
            <circle cx="9" cy="7" r="4"></circle>
          </svg>
        </div>
        <h3 class="text-lg font-semibold tracking-tight font-manrope mb-2">Family</h3>
        <p class="text-sm flex-1 text-slate-400">
          Guidance on custody, divorce, support, and family agreements to protect your loved ones.
        </p>
      </div>

      <!-- Labor Law -->
      <div class="border rounded-2xl p-8 flex flex-col bg-slate-900 border-slate-800">
        <div class="w-14 h-14 rounded-full flex items-center justify-center mb-5 bg-cyan-600/10">
          <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none"
            stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"
            data-lucide="briefcase" strokewidth="1.5" class="lucide lucide-briefcase w-7 h-7 text-cyan-400">
            <path d="M16 20V4a2 2 0 0 0-2-2h-4a2 2 0 0 0-2 2v16"></path>
            <rect width="20" height="14" x="2" y="6" rx="2"></rect>
          </svg>
        </div>
        <h3 class="text-lg font-semibold tracking-tight font-manrope mb-2">Labor</h3>
        <p class="text-sm flex-1 text-slate-400">
          Support with employment rights, contracts, disputes, and workplace protections.
        </p>
      </div>

      <!-- Rental Law -->
      <div class="border rounded-2xl p-8 flex flex-col bg-slate-900 border-slate-800">
        <div class="w-14 h-14 rounded-full flex items-center justify-center mb-5 bg-cyan-600/10">
          <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none"
            stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" data-lucide="home"
            strokewidth="1.5" class="lucide lucide-home w-7 h-7 text-cyan-400">
            <path d="M15 21v-8a1 1 0 0 0-1-1h-4a1 1 0 0 0-1 1v8"></path>
            <path
              d="M3 10a2 2 0 0 1 .709-1.528l7-5.999a2 2 0 0 1 2.582 0l7 5.999A2 2 0 0 1 21 10v9a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z">
            </path>
          </svg>
        </div>
        <h3 class="text-lg font-semibold tracking-tight font-manrope mb-2">Rental</h3>
        <p class="text-sm flex-1 text-slate-400">
          Advice on leases, tenant rights, evictions, and landlord obligations.
        </p>
      </div>

      <!-- Fraud -->
      <div class="border rounded-2xl p-8 flex flex-col bg-slate-900 border-slate-800">
        <div class="w-14 h-14 rounded-full flex items-center justify-center mb-5 bg-cyan-600/10">
          <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none"
            stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"
            data-lucide="alert-triangle" strokewidth="1.5" class="lucide lucide-alert-triangle w-7 h-7 text-cyan-400">
            <path d="m21.73 18-8-14a2 2 0 0 0-3.48 0l-8 14A2 2 0 0 0 4 21h16a2 2 0 0 0 1.73-3"></path>
            <path d="M12 9v4"></path>
            <path d="M12 17h.01"></path>
          </svg>
        </div>
        <h3 class="text-lg font-semibold tracking-tight font-manrope mb-2">Fraud</h3>
        <p class="text-sm flex-1 text-slate-400">
          Help with scams, identity theft, and fraudulent contracts to protect your interests.
        </p>
      </div>

      <!-- Business Law -->
      <div class="border rounded-2xl p-8 flex flex-col bg-slate-900 border-slate-800">
        <div class="w-14 h-14 rounded-full flex items-center justify-center mb-5 bg-cyan-600/10">
          <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none"
            stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"
            data-lucide="dollar-sign" strokewidth="1.5" class="lucide lucide-dollar-sign w-7 h-7 text-cyan-400">
            <line x1="12" x2="12" y1="2" y2="22"></line>
            <path d="M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6"></path>
          </svg>
        </div>
        <h3 class="text-lg font-semibold tracking-tight font-manrope mb-2">Business</h3>
        <p class="text-sm flex-1 text-slate-400">
          Guidance on contracts, formation, compliance, and disputes to help your business thrive.
        </p>
      </div>
    </div>
  </section>
  <section id="faq" class="max-w-7xl mx-auto pt-24 px-6 pb-24">
    <h2 class="text-3xl md:text-4xl font-semibold tracking-tight font-montserrat text-center">
      Frequently Asked Questions
    </h2>
    <p class="mt-4 max-w-2xl mx-auto text-center font-medium text-slate-400">
      Answers to common questions about our services and processes.
    </p>

    <div class="mt-16 max-w-4xl mx-auto space-y-6">
      <div class="border rounded-2xl bg-slate-900 border-slate-800">
        <button
          class="faq-toggle w-full flex items-center justify-between px-6 py-5 text-left font-manrope font-semibold text-lg tracking-tight transition-colors rounded-2xl text-slate-100 hover:bg-slate-800"
          aria-expanded="false">
          How quickly can I get legal advice?
          <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none"
            stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"
            data-lucide="chevron-down" strokewidth="1.5"
            class="lucide lucide-chevron-down w-5 h-5 transition-transform duration-200 text-cyan-400">
            <path d="m6 9 6 6 6-6"></path>
          </svg>
        </button>
        <div class="faq-content hidden px-6 pb-6 text-sm font-manrope leading-relaxed text-slate-400">
          We typically provide answers within minutes, 24/7. Our system matches you with a qualified attorney instantly.
        </div>
      </div>

      <div class="border rounded-2xl bg-slate-900 border-slate-800">
        <button
          class="faq-toggle w-full flex items-center justify-between px-6 py-5 text-left font-manrope font-semibold text-lg tracking-tight transition-colors rounded-2xl text-slate-100 hover:bg-slate-800"
          aria-expanded="false">
          Are the attorneys licensed and vetted?
          <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none"
            stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"
            data-lucide="chevron-down" strokewidth="1.5"
            class="lucide lucide-chevron-down w-5 h-5 transition-transform duration-200 text-cyan-400">
            <path d="m6 9 6 6 6-6"></path>
          </svg>
        </button>
        <div class="faq-content hidden px-6 pb-6 text-sm font-manrope leading-relaxed text-slate-400">
          Yes, all attorneys on LegalNow are thoroughly vetted and licensed to practice in their respective
          jurisdictions.
        </div>
      </div>

      <div class="border rounded-2xl bg-slate-900 border-slate-800">
        <button
          class="faq-toggle w-full flex items-center justify-between px-6 py-5 text-left font-manrope font-semibold text-lg tracking-tight transition-colors rounded-2xl text-slate-100 hover:bg-slate-800"
          aria-expanded="false">
          How is my privacy protected?
          <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none"
            stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"
            data-lucide="chevron-down" strokewidth="1.5"
            class="lucide lucide-chevron-down w-5 h-5 transition-transform duration-200 text-cyan-400">
            <path d="m6 9 6 6 6-6"></path>
          </svg>
        </button>
        <div class="faq-content hidden px-6 pb-6 text-sm font-manrope leading-relaxed text-slate-400">
          We use bank-level encryption to ensure all conversations and data remain confidential and secure.
        </div>
      </div>

      <div class="border rounded-2xl bg-slate-900 border-slate-800">
        <button
          class="faq-toggle w-full flex items-center justify-between px-6 py-5 text-left font-manrope font-semibold text-lg tracking-tight transition-colors rounded-2xl text-slate-100 hover:bg-slate-800"
          aria-expanded="false">
          What types of legal issues can I ask about?
          <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none"
            stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"
            data-lucide="chevron-down" strokewidth="1.5"
            class="lucide lucide-chevron-down w-5 h-5 transition-transform duration-200 text-cyan-400">
            <path d="m6 9 6 6 6-6"></path>
          </svg>
        </button>
        <div class="faq-content hidden px-6 pb-6 text-sm font-manrope leading-relaxed text-slate-400">
          We cover a broad range of areas including family, labor, rental, fraud, business, and more. Feel free to ask
          about your specific concern.
        </div>
      </div>
    </div>
  </section>
  <section id="cta"
    class="max-w-7xl bg-gradient-to-r rounded-3xl mr-auto ml-auto pt-24 pr-6 pb-24 pl-6 shadow-2xl from-cyan-600 via-blue-600 to-indigo-700">
    <div class="max-w-4xl mx-auto text-center">
      <h2 class="text-3xl md:text-4xl font-semibold tracking-tight font-montserrat text-white">
        Ready to get expert legal advice now?
      </h2>
      <p class="mt-4 text-lg font-medium font-manrope max-w-2xl mx-auto text-cyan-100">
        Connect instantly with licensed attorneys and get answers tailored to your needs.
      </p>
      <a href="#start"
        class="mt-10 inline-flex items-center justify-center gap-3 rounded-md font-semibold font-manrope px-8 py-4 text-lg transition-colors shadow-md bg-white text-cyan-700 hover:bg-cyan-50">
        Start your consultation
        <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none"
          stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"
          data-lucide="arrow-right" strokewidth="1.5" class="lucide lucide-arrow-right w-6 h-6">
          <path d="M5 12h14"></path>
          <path d="m12 5 7 7-7 7"></path>
        </svg>
      </a>
    </div>
  </section>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
function goToRegister() { router.push('/register') }
function goToLogin() { router.push('/login') }
function goTo(section: string) { /* router.push or scroll to section */ }

</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;700&display=swap');

body,
.v-application {
  font-family: 'Inter', 'Segoe UI', Arial, sans-serif;
}

.gradient-bg {
  min-height: 100vh;
  background: radial-gradient(ellipse at 60% 40%, #a5b4fc 0%, #6366f1 100%);
  padding: 0;
}

.app-bar-blur {
  backdrop-filter: blur(12px);
  background: rgba(38, 99, 235, 0.85) !important;
}

.main-card {
  width: 1100px;
  max-width: 98vw;
  min-height: 520px;
  background: rgba(24, 27, 44, 0.95);
  border-radius: 40px;
  box-shadow: 0 8px 48px 0 rgba(99, 102, 241, 0.18);
  margin-top: 60px;
  margin-bottom: 40px;
  overflow: visible;
}

.hero-left {
  flex: 1 1 0;
  min-width: 340px;
  max-width: 480px;
  color: #fff;
}

.hero-right {
  flex: 1 1 0;
  min-width: 340px;
  max-width: 400px;
  position: relative;
  height: 340px;
}

.avatar-network {
  position: relative;
  width: 340px;
  height: 340px;
}

.avatar-center {
  position: absolute;
  left: 50%;
  top: 50%;
  width: 120px;
  height: 120px;
  transform: translate(-50%, -50%);
  background: rgba(99, 102, 241, 0.12);
  border-radius: 50%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  box-shadow: 0 0 32px #a5b4fc44;
}

.stat-number {
  font-size: 2.2rem;
  font-weight: bold;
  color: #fff;
  text-align: center;
}

.stat-label {
  font-size: 1rem;
  color: #e0e7ff;
  text-align: center;
}

@media (max-width: 1200px) {
  .main-card {
    flex-direction: column !important;
    align-items: center !important;
    padding: 32px 8px !important;
    width: 98vw;
    min-height: 700px;
  }

  .hero-right {
    margin-top: 32px;
  }
}
</style>
