<script>
    import GenrePersonnelForm from "$lib/GenrePersonnelForm.svelte";
    import { MAX_GENRE_SEARCH_LENGTH } from '$lib/consts.js';
</script>
  
<GenrePersonnelForm type="Genre" maxlength={MAX_GENRE_SEARCH_LENGTH}/>