import React, { FormEvent, useState } from "react";
import { LockIcon, LogInIcon, ShieldCheckIcon } from "lucide-react";
import { AuthSession } from "../types/crm";

interface AuthScreenProps {
  initialSession: AuthSession;
  error?: string | null;
  loading?: boolean;
  onSubmit: (session: AuthSession) => void;
}

export function AuthScreen({ initialSession, error, loading = false, onSubmit }: AuthScreenProps) {
  const [formState, setFormState] = useState<AuthSession>(initialSession);

  const updateField =
    (field: keyof AuthSession) =>
    (event: React.ChangeEvent<HTMLInputElement>) => {
      setFormState((current) => ({ ...current, [field]: event.target.value }));
    };

  const handleSubmit = (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    onSubmit({
      ...formState,
      baseUrl: formState.baseUrl.trim().replace(/\/$/, ""),
      tenantId: formState.tenantId.trim(),
      username: formState.username.trim(),
      password: formState.password,
    });
  };

  return (
    <div className="flex min-h-screen items-center justify-center bg-[#f8f9fa] px-4 py-10">
      <div className="w-full max-w-5xl overflow-hidden rounded-3xl border border-gray-200 bg-white shadow-xl shadow-gray-200/60">
        <div className="grid lg:grid-cols-[1.05fr_0.95fr]">
          <div className="border-b border-gray-200 bg-[#fcfcfc] p-8 lg:border-b-0 lg:border-r lg:p-12">
            <div className="mb-8 flex h-12 w-12 items-center justify-center rounded-2xl bg-pink-500 text-white">
              <ShieldCheckIcon className="h-6 w-6" />
            </div>
            <p className="text-sm font-medium uppercase tracking-[0.18em] text-gray-500">
              AI-Enabled CRM
            </p>
            <h1 className="mt-3 text-4xl font-semibold leading-tight text-gray-900">
              Sign in to the backend you already built.
            </h1>
            <p className="mt-4 max-w-xl text-base leading-8 text-gray-600">
              This starter now supports the current Spring backend auth model:
              base URL, tenant header, and Basic Auth credentials.
            </p>

            <div className="mt-10 space-y-4">
              {[
                "Works with the current local Basic Auth setup",
                "Designed to move cleanly to JWT/OAuth2 later",
                "Starts by wiring identity, dashboard, leads, contacts, accounts, and opportunities",
              ].map((item) => (
                <div key={item} className="rounded-2xl border border-gray-200 bg-white px-4 py-4 text-sm text-gray-700">
                  {item}
                </div>
              ))}
            </div>
          </div>

          <div className="p-8 lg:p-12">
            <div className="mb-8 flex items-center gap-3">
              <div className="flex h-10 w-10 items-center justify-center rounded-full bg-gray-100 text-gray-500">
                <LockIcon className="h-4 w-4" />
              </div>
              <div>
                <div className="text-lg font-semibold text-gray-900">Authentication</div>
                <div className="text-sm text-gray-500">Use your current backend credentials</div>
              </div>
            </div>

            <form onSubmit={handleSubmit} className="space-y-5">
              <label className="block">
                <span className="mb-2 block text-sm font-medium text-gray-700">Backend URL</span>
                <input
                  value={formState.baseUrl}
                  onChange={updateField("baseUrl")}
                  className="w-full rounded-xl border border-gray-200 bg-gray-50 px-4 py-3 text-sm text-gray-900 outline-none transition focus:border-gray-400 focus:bg-white"
                  placeholder="http://localhost:8080"
                />
              </label>

              <label className="block">
                <span className="mb-2 block text-sm font-medium text-gray-700">Tenant ID</span>
                <input
                  value={formState.tenantId}
                  onChange={updateField("tenantId")}
                  className="w-full rounded-xl border border-gray-200 bg-gray-50 px-4 py-3 text-sm text-gray-900 outline-none transition focus:border-gray-400 focus:bg-white"
                  placeholder="tenant-demo"
                />
              </label>

              <div className="grid gap-5 sm:grid-cols-2">
                <label className="block">
                  <span className="mb-2 block text-sm font-medium text-gray-700">Username</span>
                  <input
                    value={formState.username}
                    onChange={updateField("username")}
                    className="w-full rounded-xl border border-gray-200 bg-gray-50 px-4 py-3 text-sm text-gray-900 outline-none transition focus:border-gray-400 focus:bg-white"
                    placeholder="local-dev"
                  />
                </label>

                <label className="block">
                  <span className="mb-2 block text-sm font-medium text-gray-700">Password</span>
                  <input
                    type="password"
                    value={formState.password}
                    onChange={updateField("password")}
                    className="w-full rounded-xl border border-gray-200 bg-gray-50 px-4 py-3 text-sm text-gray-900 outline-none transition focus:border-gray-400 focus:bg-white"
                    placeholder="local-dev-pass"
                  />
                </label>
              </div>

              {error ? (
                <div className="rounded-xl border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">
                  {error}
                </div>
              ) : null}

              <button
                type="submit"
                disabled={loading}
                className="flex w-full items-center justify-center gap-2 rounded-xl bg-black px-4 py-3 text-sm font-medium text-white transition-colors hover:bg-gray-800 disabled:cursor-not-allowed disabled:bg-gray-400"
              >
                <LogInIcon className="h-4 w-4" />
                {loading ? "Connecting..." : "Sign in"}
              </button>
            </form>
          </div>
        </div>
      </div>
    </div>
  );
}
