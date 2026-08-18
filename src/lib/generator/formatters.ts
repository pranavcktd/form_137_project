export function formatDateDDMMYYYY(date: Date): string {
  const dd = String(date.getDate()).padStart(2, "0");
  const mm = String(date.getMonth() + 1).padStart(2, "0");
  const yyyy = date.getFullYear();
  return `${dd}${mm}${yyyy}`;
}

export function formatDecimal2(value: number | string | null | undefined): string {
  if (value === null || value === undefined || value === "") return "";
  const num = typeof value === "string" ? Number(value) : value;
  return num.toFixed(2);
}

export function formatIntOrEmpty(value: number | null | undefined): string {
  if (value === null || value === undefined) return "";
  return String(value);
}
