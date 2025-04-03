<script>
  import { Button, Modal, Container } from '@sveltestrap/sveltestrap';
  import { createEventDispatcher } from 'svelte';

  export let modalTitle;
  export let modalBody;
  export let buttonText;
  export let buttonColor = 'danger';

  let open = true;

  const dispatch = createEventDispatcher();

  const toggle = () => {
    open = !open;
    dispatch('toggle', { open });
  };

  const confirm = () => {
    dispatch('confirm');
    toggle();
  };

</script>

<div>
  <Modal body header={modalTitle} isOpen={open} {toggle}>
    <p class="text-center">
      {modalBody}
    </p>
    <slot></slot>
    <Container class="text-center">
      <Button color={buttonColor} class="mt-3" on:click={confirm}>{buttonText}</Button>
    </Container>
  </Modal>
</div>