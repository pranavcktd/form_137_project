import state from "@/schemas/24g-f137/v1_9/annexures/state.json";
import formType from "@/schemas/24g-f137/v1_9/annexures/formType.json";
import natureOfPayment from "@/schemas/24g-f137/v1_9/annexures/natureOfPayment.json";
import ddoMapping from "@/schemas/24g-f137/v1_9/annexures/ddoMapping.json";
import type { AnnexureKey } from "@/lib/validation/annexureLookup";

export interface AnnexureOption {
  code: string;
  label: string;
}

/** Client-safe {code,label} option lists for the annexures DDO fields reference. */
export function annexureOptions(key: AnnexureKey): AnnexureOption[] {
  switch (key) {
    case "state":
      return state.map((s) => ({ code: s.code, label: s.name }));
    case "formType":
      return formType.map((f) => ({ code: f.code, label: f.name }));
    case "natureOfPayment":
      return natureOfPayment.map((n) => ({ code: n.sectionCode, label: `${n.name} (${n.sectionCode})` }));
    case "ddoMapping":
      return ddoMapping.map((d) => ({ code: d.code, label: d.name }));
    default:
      return [];
  }
}
