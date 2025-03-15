<script>
  import GenrePersonnelForm from "$lib/GenrePersonnelForm.svelte";
  import { MAX_PERSON_SEARCH_LENGTH } from '$lib/consts.js';
</script>

<GenrePersonnelForm type="Personnel" maxlength={MAX_PERSON_SEARCH_LENGTH}/>