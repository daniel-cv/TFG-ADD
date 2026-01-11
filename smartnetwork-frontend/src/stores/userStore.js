import { defineStore } from "pinia";
import axios from "axios";
import api from "@/services/api";   // ← usa la instancia global

export const useUserStore = defineStore("user", {
  state: () => ({
    usuarios: [],
    usuarioActual: null,
    autenticado: false,
    mensaje: "",
  }),

  actions: {
    async fetchUsuarios() {
      try {
        const response = await api.get("/api/usuario/findAll");
        this.usuarios = response.data;
      } catch (error) {
        console.error(error);
        this.mensaje = "Error al obtener usuarios";
      }
    },

    async crearUsuario(usuario) {
      try {
        const response = await api.post("/api/usuario/create", usuario);
        this.mensaje = `Usuario creado: ${response.data.username}`;
        return response.data;
      } catch (error) {
        console.error(error);
        this.mensaje = `Error al crear usuario: ${error.response.data.message}`; 

        throw error;
      }
    },

    async login(username, password) {
      try {
        const response = await api.post("/auth/login", {
          username,
          password
        });

        const token = response.data.token;

        this.usuarioActual = { username, token };
        this.autenticado = true;

        // Añadir token a TODAS las peticiones
        api.defaults.headers.common["Authorization"] = "Bearer " + token;

        return this.usuarioActual;

      } catch (error) {
        console.error(error);
        this.mensaje = "Usuario o contraseña incorrectos";
        throw error;
      }
    },

    logout() {
      this.usuarioActual = null;
      this.autenticado = false;
      this.mensaje = "";

      delete api.defaults.headers.common["Authorization"];
    }
  },
});
