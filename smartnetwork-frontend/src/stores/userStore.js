import { defineStore } from "pinia";
import axios from "axios";

// Crear instancia de Axios centralizada
const api = axios.create({
  baseURL: "http://localhost:8080/usuario", // Base URL de tu backend Spring
  headers: { "Content-Type": "application/json" },
});

export const useUserStore = defineStore("user", {
  state: () => ({
    usuarios: [],
    usuarioActual: null,
    mensaje: "",
  }),
  actions: {
    // Traer todos los usuarios
    async fetchUsuarios() {
      try {
        const response = await api.get("/findAll");
        this.usuarios = response.data;
      } catch (error) {
        console.error(error);
        this.mensaje = "Error al obtener usuarios";
      }
    },

    // Crear un nuevo usuario
    async crearUsuario(usuario) {
      try {
        const response = await api.post("/create", usuario);
        this.mensaje = `Usuario creado: ${response.data.username}`;
        return response.data;
      } catch (error) {
        console.error(error);
        this.mensaje = "Error al crear usuario: ";
        throw error;
      }
    },

    // Login de usuario
    async login(username, password) {
      try {
        const response = await api.post("/login", { username, password });
        this.usuarioActual = response.data;
        this.mensaje = `Bienvenido, ${response.data.username}`;
        return response.data;
      } catch (error) {
        console.error(error);
        this.mensaje = "Usuario o contraseña incorrectos";
        throw error;
      }
    },
  },
});
