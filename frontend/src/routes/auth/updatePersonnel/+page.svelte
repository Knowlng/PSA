<script>
    import GenrePersonnelUpdate from "$lib/GenrePersonnelUpdate.svelte";
    import { MAX_PERSON_SEARCH_LENGTH } from '$lib/consts.js';
</script>
  
<GenrePersonnelUpdate type="Personnel" maxlength={MAX_PERSON_SEARCH_LENGTH}/>