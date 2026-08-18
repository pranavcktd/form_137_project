import { useEffect, useState } from "react";

export const DEFAULT_PAGE_SIZE = 20;

/**
 * Client-side pagination over an already-fetched/filtered array — matches
 * how this app already does search (filter the full fetched list in the
 * browser rather than round-tripping to the server).
 *
 * Pass whatever the list is filtered/sorted by as `resetKey` (e.g. the
 * search term) so changing it snaps back to page 1 instead of landing on
 * a now-empty later page.
 */
export function usePagination<T>(items: T[], pageSize: number = DEFAULT_PAGE_SIZE, resetKey?: unknown) {
  const [page, setPage] = useState(1);

  // eslint-disable-next-line react-hooks/exhaustive-deps
  useEffect(() => setPage(1), [resetKey]);

  const totalPages = Math.max(1, Math.ceil(items.length / pageSize));
  const safePage = Math.min(page, totalPages);
  const start = (safePage - 1) * pageSize;
  const pageItems = items.slice(start, start + pageSize);

  return { page: safePage, setPage, totalPages, pageItems, totalItems: items.length, pageSize };
}
