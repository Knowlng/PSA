<script>
  import GenrePersonnelUpdate from "$lib/GenrePersonnelUpdate.svelte";
  import { MAX_GENRE_SEARCH_LENGTH } from '$lib/consts.js';
</script>

<GenrePersonnelUpdate type="Genre" maxlength={MAX_GENRE_SEARCH_LENGTH}/>