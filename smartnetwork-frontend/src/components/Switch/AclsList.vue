<template>
  <div class="service-list">

    <!-- HEADER -->
    <div class="table-header">
      <h2>ACLs</h2>

      <button class="add-btn" @click="mostrarFormulario = !mostrarFormulario">
        + Nueva ACL
      </button>
    </div>

    <!-- FORM CREAR ACL -->
    <div v-if="mostrarFormulario" class="formulario-inline">
      <input v-model="nuevaAcl.nombre" placeholder="Nombre ACL" class="input-edit" />

      <button class="btn-guardar" @click="crearAcl">
        Crear
      </button>
    </div>

    <!-- TABLA ACL -->
    <table class="professional-table">

      <thead>
        <tr>
          <th>Nombre</th>
          <th>Reglas</th>
          <th>Acciones</th>
        </tr>
      </thead>

      <tbody>

        <template v-for="acl in aclStore.acls" :key="acl.id">

          <!-- ACL ROW -->
          <tr>

            <td @click="toggleAcl(acl.id)" style="cursor:pointer">
              <span class="badge blue">
                {{ acl.nombre }}
              </span>
            </td>

            <td>
              {{ acl.reglas?.length || 0 }} reglas
            </td>

            <td>
              <button class="btn-guardar" @click="mostrarReglaForm(acl.id)">
                + Regla
              </button>

              <button class="btn-borrar" @click="borrarAcl(acl.id)">
                Borrar
              </button>
            </td>

          </tr>

          <!-- 🔥 REGLAS EXPANDIDAS -->
          <tr v-if="aclExpandida === acl.id">
            <td colspan="3">

              <!-- FORM REGLA -->
              <div v-if="aclSeleccionada === acl.id" class="formulario-inline">

                <select v-model="nuevaRegla.accion" class="input-edit">
                  <option value="permit">permit</option>
                  <option value="deny">deny</option>
                </select>

                <input v-model="nuevaRegla.origen" placeholder="Origen (any)" class="input-edit" />
                <input v-model="nuevaRegla.destino" placeholder="Destino (any)" class="input-edit" />

                <button class="btn-guardar" @click="crearRegla">
                  Añadir
                </button>

              </div>

              <!-- LISTA REGLAS -->
              <table class="professional-table nested-table">

                <thead>
                  <tr>
                    <th>#</th>
                    <th>Acción</th>
                    <th>Origen</th>
                    <th>Destino</th>
                    <th></th>
                  </tr>
                </thead>

                <tbody>
                  <tr v-for="r in acl.reglas" :key="r.id">

                    <td>{{ r.orden }}</td>

                    <td>
                      <span :class="r.accion === 'permit' ? 'badge green' : 'badge red'">
                        {{ r.accion }}
                      </span>
                    </td>

                    <td>{{ r.origen }}</td>
                    <td>{{ r.destino }}</td>

                    <td>
                      <button class="btn-borrar mini" @click="borrarRegla(r.id)">
                        x
                      </button>
                    </td>

                  </tr>
                </tbody>

              </table>

            </td>
          </tr>

        </template>

      </tbody>

    </table>

  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useAclStore } from '@/stores/Switches/aclStore'
import { useDispositivoSeleccionadoStore } from '@/stores/dispositivoSeleccionadoStore'

const aclStore = useAclStore()
const dispositivoStore = useDispositivoSeleccionadoStore()

const mostrarFormulario = ref(false)
const aclSeleccionada = ref(null)
const aclExpandida = ref(null)

const nuevaAcl = ref({
  nombre: ''
})

const nuevaRegla = ref({
  accion: 'permit',
  origen: 'any',
  destino: 'any',
  orden: 10
})

// 🔥 TOGGLE
const toggleAcl = (id) => {
  aclExpandida.value = aclExpandida.value === id ? null : id
}

// 🔥 CREAR ACL
const crearAcl = async () => {
  await aclStore.crearAcl({
    ...nuevaAcl.value,
    dispositivoId: dispositivoStore.dispositivo.id
  })

  nuevaAcl.value.nombre = ''
  mostrarFormulario.value = false

  await aclStore.cargarAcls(dispositivoStore.dispositivo.id)
}

// 🔥 BORRAR ACL
const borrarAcl = async (id) => {
  await aclStore.eliminarAcl(id)
  aclStore.cargarAcls(dispositivoStore.dispositivo.id)
}

// 🔥 FORM REGLA
const mostrarReglaForm = (aclId) => {
  aclSeleccionada.value = aclId
  aclExpandida.value = aclId
}

// 🔥 CREAR REGLA
const crearRegla = async () => {
  await aclStore.agregarRegla(aclSeleccionada.value, nuevaRegla.value)

  nuevaRegla.value = {
    accion: 'permit',
    origen: 'any',
    destino: 'any',
    orden: 10
  }

  aclSeleccionada.value = null

  await aclStore.cargarAcls(dispositivoStore.dispositivo.id)
}

// 🔥 BORRAR REGLA
const borrarRegla = async (id) => {
  await aclStore.eliminarRegla(id)
  aclStore.cargarAcls(dispositivoStore.dispositivo.id)
}

// 🔥 LOAD
onMounted(() => {
  aclStore.cargarAcls(dispositivoStore.dispositivo.id)
})
</script>

<style scoped>

/* reutilizas TODO lo de VLAN */

.service-list { width: 100%;
color: black;}

.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.add-btn {
  background: #3b82f6;
  color: white;
  border-radius: 8px;
  padding: 8px 14px;
  border: none;
  cursor: pointer;
}

.professional-table {
  width: 100%;
  background: white;
  border-radius: 12px;
  border-collapse: collapse;
  color: black;
}

.professional-table th,
.professional-table td {
  padding: 14px;

}

.formulario-inline {
  display: flex;
  gap: 12px;
  padding: 16px;
  background: #f8fafc;
  border-radius: 12px;
  margin-bottom: 12px;
  color: black;
}

.input-edit {
  padding: 6px 10px;
  border-radius: 6px;
  border: 1px solid #e5e7eb;
  color: black;
}

.btn-guardar {
  background: #22c55e;
  color: white;
  border: none;
  padding: 6px 12px;
  border-radius: 6px;
}

.btn-borrar {
  background: #ef4444;
  color: white;
  border: none;
  padding: 6px 12px;
  border-radius: 6px;

}

.mini {
  padding: 2px 8px;

}

.badge {
  padding: 4px 10px;
  border-radius: 10px;

}

.blue { background: #dbeafe; }
.green { background: #dcfce7; }
.red { background: #fee2e2; }

/* 🔥 tabla interna */
.nested-table {
  margin-top: 10px;
}

</style>
