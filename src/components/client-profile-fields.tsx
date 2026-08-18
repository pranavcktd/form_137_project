"use client";

import states from "@/schemas/24g-f137/v1_9/annexures/state.json";
import ministries from "@/schemas/24g-f137/v1_9/annexures/ministry.json";
import subMinistries from "@/schemas/24g-f137/v1_9/annexures/subMinistry.json";
import countries from "@/schemas/24g-f137/v1_9/annexures/country.json";
import { FieldLabel, inputClass } from "@/components/ui";

export type ClientProfileValues = {
  ain: string;
  tan: string;
  ministryName: string;
  subMinistryName: string;
  departmentName: string;
  govtCategory: "CENTRAL" | "STATE";
  countryCode: string;
  responsiblePersonName: string;
  responsiblePersonFirstName: string;
  responsiblePersonMiddleName: string;
  responsiblePersonLastName: string;
  responsiblePersonDesignation: string;
  responsiblePersonAddress1: string;
  responsiblePersonAddress2: string;
  responsiblePersonAddress3: string;
  responsiblePersonAddress4: string;
  responsiblePersonCity: string;
  responsiblePersonState: string;
  responsiblePersonPin: string;
  responsiblePersonStdCode: string;
  responsiblePersonPhone: string;
  responsiblePersonMobile: string;
  responsiblePersonEmail: string;
};

export function clientProfileDefaults(): ClientProfileValues {
  return {
    ain: "",
    tan: "",
    ministryName: "",
    subMinistryName: "",
    departmentName: "",
    govtCategory: "CENTRAL",
    countryCode: "",
    responsiblePersonName: "",
    responsiblePersonFirstName: "",
    responsiblePersonMiddleName: "",
    responsiblePersonLastName: "",
    responsiblePersonDesignation: "",
    responsiblePersonAddress1: "",
    responsiblePersonAddress2: "",
    responsiblePersonAddress3: "",
    responsiblePersonAddress4: "",
    responsiblePersonCity: "",
    responsiblePersonState: "",
    responsiblePersonPin: "",
    responsiblePersonStdCode: "",
    responsiblePersonPhone: "",
    responsiblePersonMobile: "",
    responsiblePersonEmail: "",
  };
}

function Field({
  label,
  children,
}: {
  label: string;
  children: React.ReactNode;
}) {
  return (
    <div className="space-y-1">
      <FieldLabel>{label}</FieldLabel>
      {children}
    </div>
  );
}

export function ClientProfileFields({
  values,
  onChange,
}: {
  values: ClientProfileValues;
  onChange: (values: ClientProfileValues) => void;
}) {
  const set = <K extends keyof ClientProfileValues>(
    key: K,
    value: ClientProfileValues[K],
  ) => onChange({ ...values, [key]: value });

  return (
    <div className="space-y-8">
      <div>
        <h3 className="mb-3 text-sm font-semibold text-slate-900">
          Accounts Office Details
        </h3>
        <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
          <Field label="Accounts Office Identification No. (AIN)">
            <input
              className={inputClass}
              required
              maxLength={7}
              value={values.ain}
              onChange={(e) => set("ain", e.target.value.replace(/\D/g, ""))}
              placeholder="7 digit AIN"
            />
          </Field>

          <Field label="TAN (optional)">
            <input
              className={inputClass}
              maxLength={10}
              value={values.tan}
              onChange={(e) => set("tan", e.target.value.toUpperCase())}
              placeholder="MUMD12345A"
            />
          </Field>

          <Field label="Government Category">
            <select
              className={inputClass}
              value={values.govtCategory}
              onChange={(e) =>
                set("govtCategory", e.target.value as "CENTRAL" | "STATE")
              }
            >
              <option value="CENTRAL">Central</option>
              <option value="STATE">State</option>
            </select>
          </Field>

          {values.govtCategory === "CENTRAL" && (
            <Field label="Ministry Name">
              <select
                className={inputClass}
                value={values.ministryName}
                onChange={(e) => set("ministryName", e.target.value)}
              >
                <option value="">Select ministry</option>
                {ministries.map((m) => (
                  <option key={m.code} value={m.code}>
                    {m.name}
                  </option>
                ))}
              </select>
            </Field>
          )}

          {values.govtCategory === "CENTRAL" && (
            <Field label="Sub Ministry Name">
              <select
                className={inputClass}
                value={values.subMinistryName}
                onChange={(e) => set("subMinistryName", e.target.value)}
              >
                <option value="">Select sub ministry</option>
                {subMinistries.map((m) => (
                  <option key={m.code} value={m.code}>
                    {m.name}
                  </option>
                ))}
              </select>
            </Field>
          )}

          <Field label="Department Name">
            <input
              className={inputClass}
              required
              value={values.departmentName}
              onChange={(e) => set("departmentName", e.target.value)}
            />
          </Field>
        </div>
      </div>

      <div>
        <h3 className="mb-3 text-sm font-semibold text-slate-900">
          Responsible Person Details
        </h3>
        <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
          <Field label="Name (FY2025-26 and earlier filings)">
            <input
              className={inputClass}
              value={values.responsiblePersonName}
              onChange={(e) => set("responsiblePersonName", e.target.value)}
            />
          </Field>
          <Field label="Designation">
            <input
              className={inputClass}
              required
              value={values.responsiblePersonDesignation}
              onChange={(e) =>
                set("responsiblePersonDesignation", e.target.value)
              }
            />
          </Field>
          <Field label="First Name (FY2026-27 onward filings)">
            <input
              className={inputClass}
              value={values.responsiblePersonFirstName}
              onChange={(e) => set("responsiblePersonFirstName", e.target.value)}
            />
          </Field>
          <Field label="Middle Name (FY2026-27 onward filings)">
            <input
              className={inputClass}
              value={values.responsiblePersonMiddleName}
              onChange={(e) => set("responsiblePersonMiddleName", e.target.value)}
            />
          </Field>
          <Field label="Last Name (FY2026-27 onward filings)">
            <input
              className={inputClass}
              value={values.responsiblePersonLastName}
              onChange={(e) => set("responsiblePersonLastName", e.target.value)}
            />
          </Field>
          <Field label="Country Code (FY2026-27 onward filings)">
            <select
              className={inputClass}
              value={values.countryCode}
              onChange={(e) => set("countryCode", e.target.value)}
            >
              <option value="">Select country</option>
              {countries.map((c) => (
                <option key={c.code} value={c.code}>
                  {c.name}
                </option>
              ))}
            </select>
          </Field>
          <Field label="Address Line 1">
            <input
              className={inputClass}
              required
              value={values.responsiblePersonAddress1}
              onChange={(e) => set("responsiblePersonAddress1", e.target.value)}
            />
          </Field>
          <Field label="Address Line 2">
            <input
              className={inputClass}
              value={values.responsiblePersonAddress2}
              onChange={(e) => set("responsiblePersonAddress2", e.target.value)}
            />
          </Field>
          <Field label="City">
            <input
              className={inputClass}
              required
              value={values.responsiblePersonCity}
              onChange={(e) => set("responsiblePersonCity", e.target.value)}
            />
          </Field>
          <Field label="State">
            <select
              className={inputClass}
              required
              value={values.responsiblePersonState}
              onChange={(e) => set("responsiblePersonState", e.target.value)}
            >
              <option value="">Select state</option>
              {states.map((s) => (
                <option key={s.code} value={s.code}>
                  {s.name}
                </option>
              ))}
            </select>
          </Field>
          <Field label="PIN Code">
            <input
              className={inputClass}
              required
              maxLength={6}
              value={values.responsiblePersonPin}
              onChange={(e) =>
                set("responsiblePersonPin", e.target.value.replace(/\D/g, ""))
              }
            />
          </Field>
          <Field label="Phone (STD Code + Number)">
            <div className="flex gap-2">
              <input
                className={`${inputClass} w-20`}
                placeholder="STD"
                maxLength={5}
                value={values.responsiblePersonStdCode}
                onChange={(e) =>
                  set(
                    "responsiblePersonStdCode",
                    e.target.value.replace(/\D/g, ""),
                  )
                }
              />
              <input
                className={inputClass}
                placeholder="Phone number"
                maxLength={10}
                value={values.responsiblePersonPhone}
                onChange={(e) =>
                  set(
                    "responsiblePersonPhone",
                    e.target.value.replace(/\D/g, ""),
                  )
                }
              />
            </div>
          </Field>
          <Field label="Mobile Number">
            <input
              className={inputClass}
              maxLength={10}
              value={values.responsiblePersonMobile}
              onChange={(e) =>
                set(
                  "responsiblePersonMobile",
                  e.target.value.replace(/\D/g, ""),
                )
              }
            />
          </Field>
          <Field label="Email">
            <input
              type="email"
              className={inputClass}
              required
              value={values.responsiblePersonEmail}
              onChange={(e) => set("responsiblePersonEmail", e.target.value)}
            />
          </Field>
        </div>
      </div>
    </div>
  );
}
