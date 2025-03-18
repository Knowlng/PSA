<script>
  import { Button, Modal, Container } from '@sveltestrap/sveltestrap';

  export let modalTitle;
  export let modalBody;

  let open = true;
import { createEventDispatcher } from 'svelte';

const dispatch = createEventDispatcher();

const toggle = () => {
    open = !open;
    dispatch('toggle', { open });
};

const confirmDelete = () => {
    dispatch('confirmDelete');
    toggle();
};

</script>

<div>
  <Modal body header={modalTitle} isOpen={open} {toggle}>
    <p class="text-center">
        {modalBody}
    </p>
    <Container class="text-center">
        <Button color="danger" class="mt-3" on:click={confirmDelete}>Delete</Button>
    </Container>
  </Modal>
</div>