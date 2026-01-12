<template>
  <v-table>
    <thead>
      <tr>
        <th>Nombre</th>
        <th>Origen</th>
        <th>Destino</th>
        <th>ipOrigen</th>
        <th>ipDestino</th>
        <th>Servicio</th>
      </tr>
    </thead>

    <tbody>
      <tr v-for="r in reglas" :key="r.id">
        <td>{{ r.nombre }}</td>
        <td>{{ r.origen }}</td>
        <td>{{ r.destino }}</td>
        <td>{{ r.iporigen }}</td>
        <td>{{ r.ipdestino }}</td>
        <td>{{ r.servicio }}</td>
      </tr>
    </tbody>
  </v-table>
</template>

<script setup>
defineProps({
  reglas: Array
})
</script>