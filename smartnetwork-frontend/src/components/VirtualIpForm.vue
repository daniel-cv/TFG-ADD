<template>
  <v-form @submit.prevent="handleSubmit">

    <!-- NAME -->
    <v-text-field
      v-model="name"
      label="Nombre"
      prepend-inner-icon="mdi-swap-horizontal"
      variant="outlined"
      class="mb-3"
      :disabled="virtualIpEdit"
      required
    />

    <!-- INTERFAZ EXTERNA (extintf) -->
    <v-select
      v-model="interfazId"
      :items="interfaces"
      item-title="name"
      item-value="id"
      label="Interfaz externa"
      variant="outlined"
      class="mb-3"
      required
    />

    <!-- TYPE -->
    <v-select
      v-model="type"
      :items="['static-nat']"
      label="Tipo"
      variant="outlined"
      class="mb-3"
      :disabled="virtualIpEdit"
      required
    />

    <!-- EXTERNAL IP -->
    <v-text-field
      v-model="externalIp"
      label="IP Externa (extip)"
      variant="outlined"
      class="mb-3"
      required
    />

    <!-- INTERNAL IP -->
    <v-text-field
      v-model="internalIp"
      label="IP Interna (mappedip)"
      variant="outlined"
      class="mb-3"
      required
    />

    <!-- COMMENTS -->
    <v-textarea
      v-model="comments"
      label="Comentarios"
      variant="outlined"
      class="mb-3"
    />

    <v-btn color="primary" size="large" block type="submit">
       {{ virtualIpEdit ? 'Actualizar VirtualIP' : 'Crear VirtualIP' }}
    </v-btn>

    <v-btn
      variant="outlined"
      size="large"
      block
      class="mt-2"
      @click="$emit('cancelar')"
    >
      Cancelar
    </v-btn>

    <p v-if="mensaje" class="mt-3 text-center">{{ mensaje }}</p>

  </v-form>
</template>
<script setup>
import { ref, onMounted } from "vue";
import { useRoute } from "vue-router";
import { useVirtualIpStore } from "@/stores/virtualIpStore";
import { useInterfazStore } from "@/stores/interfazStore";

const props = defineProps({
  dispositivoId: { type: Number, required: true },
  virtualIpEdit: { type: Object, default: null }
})
const route = useRoute();
const emit = defineEmits(['creada', 'cancelar'])

const virtualIpStore = useVirtualIpStore();
const interfazStore = useInterfazStore();

const name = ref("");
const type = ref("static-nat");
const externalIp = ref("");
const internalIp = ref("");
const comments = ref("");
const interfazId = ref(null);

const interfaces = ref([]);
const mensaje = ref("");


onMounted(() => {
 
  if (props.virtualIpEdit) {
    name.value = props.virtualIpEdit.name;
    type.value = props.virtualIpEdit.type || "static-nat";
    externalIp.value = props.virtualIpEdit.externalIp;
    internalIp.value = props.virtualIpEdit.internalIp;
    comments.value = props.virtualIpEdit.comments;
    interfazId.value = props.virtualIpEdit.interfazId;
  }
});

const handleSubmit = async () => {
  try {
    const payload = {
      name: name.value,
      type: type.value,
      externalIp: externalIp.value,
      internalIp: internalIp.value,
      comments: comments.value,
      interfazId: interfazId.value,
      dispositivoId: props.dispositivoId
    };

    if (props.virtualIpEdit) {
      await virtualIpStore.actualizarVirtualIp(props.virtualIpEdit.id, payload)
      mensaje.value = 'VirtualIP actualizada correctamente'
    } else {
      await virtualIpStore.crearVirtualIp(payload)
      mensaje.value = 'VirtualIP creada correctamente'
    }

    emit('creada')
  } catch (error) {
    mensaje.value = props.virtualIpEdit ? 'Error al actualizar VirtualIP' : 'Error al crear VirtualIP'
  }
};

</script>
