<script>
  import GenrePersonnelCreate from "$lib/GenrePersonnelCreate.svelte";
  import { MAX_GENRE_SEARCH_LENGTH } from '$lib/consts.js';
</script>

<GenrePersonnelCreate type="Genre" maxlength={MAX_GENRE_SEARCH_LENGTH}/>