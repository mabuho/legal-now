// store/janusSessionStore.ts
import { defineStore } from 'pinia';

export const useJanusSessionStore = defineStore('janusSessions', {
    state: () => ({
        sessions: {} as Record<string, {
            janus: any;
            plugin: any;
            roomId: number;
            userId: string;
            mensajes: any[];
        }>
    }),

    actions: {
        addSession(chatId: string, sessionData: any) {
            this.sessions[chatId] = sessionData;
        },
        getSession(chatId: string) {
            return this.sessions[chatId];
        },
        removeSession(chatId: string) {
            if (this.sessions[chatId]?.janus.value) {
                this.sessions[chatId].janus.value.destroy();
            }
            delete this.sessions[chatId];
        },
        listChatIds() {
            return Object.keys(this.sessions);
        }
    }
});
