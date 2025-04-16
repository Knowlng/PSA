import { writable } from 'svelte/store';
import { DEFAULT_PAGE_SIZE } from '$lib/consts.js';

export const filterStore = writable({
  movieName: '',
  ageRating: '',
  fromDate: '',
  toDate: '',
  minGross: '',
  maxGross: '',
  genreName: '',
  actorName: '',
  minRating: '',
  maxRating: '',
  selectedAgeRatings: [],
  genreArray: [],
  actorArray: [],
  perPage: DEFAULT_PAGE_SIZE.toString(),
  totalPages: 0,
  totalEntries: 0,
  currentPage: 1
});
