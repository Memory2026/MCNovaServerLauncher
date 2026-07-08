<template>
  <RouterView v-slot="{ Component }">

    <transition
      name="page-fade"
      mode="out-in"
    >

      <component
        :is="Component"
      />

    </transition>

  </RouterView>

</template>


<style>

.page-fade-enter-from {
  opacity:0;
  transform:translateY(4px);
}


.page-fade-leave-to {
  opacity:0;
  transform:translateY(-4px);
}


.page-fade-enter-active,
.page-fade-leave-active {

  transition:
    opacity .3s cubic-bezier(0.4,0,0.2,1),
    transform .3s cubic-bezier(0.4,0,0.2,1);

}

</style>
