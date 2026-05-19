/// <reference types="vite/client" />
/// <reference types="radix-vue/types" />

declare module '*.vue' {
    import type { DefineCompponent } from 'vue'
    const component: DefineCompponent<{}, {}, any>
    export default component
}