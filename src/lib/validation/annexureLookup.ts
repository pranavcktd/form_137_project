import state from "@/schemas/24g-f137/v1_9/annexures/state.json";
import ddoCategory from "@/schemas/24g-f137/v1_9/annexures/ddoCategory.json";
import ministry from "@/schemas/24g-f137/v1_9/annexures/ministry.json";
import subMinistry from "@/schemas/24g-f137/v1_9/annexures/subMinistry.json";
import natureOfPayment from "@/schemas/24g-f137/v1_9/annexures/natureOfPayment.json";
import ddoMapping from "@/schemas/24g-f137/v1_9/annexures/ddoMapping.json";
import month from "@/schemas/24g-f137/v1_9/annexures/month.json";
import country from "@/schemas/24g-f137/v1_9/annexures/country.json";
import title from "@/schemas/24g-f137/v1_9/annexures/title.json";
import formType from "@/schemas/24g-f137/v1_9/annexures/formType.json";
import revisionMode from "@/schemas/24g-f137/v1_9/annexures/revisionMode.json";

const ANNEXURES = {
  state,
  ddoCategory,
  ministry,
  subMinistry,
  ddoMapping,
  month,
  country,
  title,
  formType,
  revisionMode,
} as const;

export type AnnexureKey = keyof typeof ANNEXURES | "natureOfPayment";

export function getAnnexureCodes(key: AnnexureKey): string[] {
  if (key === "natureOfPayment") {
    return natureOfPayment.map((entry) => entry.sectionCode);
  }
  const annexure = ANNEXURES[key];
  return annexure.map((entry) => entry.code);
}
