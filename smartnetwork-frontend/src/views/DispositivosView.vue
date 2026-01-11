<template>
  <v-container>
    <v-row>
      <v-col
        v-for="d in dispositivos"
        :key="d.id"
        cols="12"
        md="4"
      >
        <v-card elevation="4">
          <v-card-title>
            {{ d.nombre }}
          </v-card-title>

          <v-card-subtitle>
            {{ d.tipo }} · {{ d.fabricante }}
          </v-card-subtitle>

          <v-card-text>
            IP: {{ d.ip }} <br>
            Estado: {{ d.estado }}
          </v-card-text>

          <v-card-actions>
            <v-btn color="primary">
              Configurar
            </v-btn>
          </v-card-actions>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useDispositivoStore } from '@/stores/dispositivoStore'

const dispositivos = ref([])
const dispositivoStore = useDispositivoStore()

onMounted(async () => {
  await dispositivoStore.getMisDispositivos()
  dispositivos.value = dispositivoStore.dispositivos
})
</script>
