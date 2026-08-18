import { AppShell } from "@/components/app-shell";
import { PageHeader } from "@/components/ui";
import { NewTaxProfessionalForm } from "./client";

export default function NewTaxProfessionalPage() {
  return (
    <AppShell firmName="Platform Admin">
      <PageHeader
        title="Onboard a Tax Professional Firm"
        subtitle="Create the firm and its first admin login. The firm's admin can then add their own clients."
      />
      <NewTaxProfessionalForm />
    </AppShell>
  );
}
