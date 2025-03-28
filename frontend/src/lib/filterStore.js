import { writable } from 'svelte/store';

export const filterStore = writable({
  movieName: '',
  ageRating: '',
  fromDate: '',
  toDate: '',
  minGross: '',
  maxGross: '',
  genreName: '',
  actorName: '',
  selectedAgeRatings: [],
  genreArray: [],
  actorArray: [],
  perPage: "10",
  totalPages: 0,
  totalEntries: 0,
  currentPage: 1
});
