<script>
  import GenrePersonnelCreate from "$lib/GenrePersonnelCreate.svelte";
  import { MAX_PERSON_SEARCH_LENGTH } from '$lib/consts.js';
</script>

<GenrePersonnelCreate type="Personnel" maxlength={MAX_PERSON_SEARCH_LENGTH}/>