import React, { FormEvent, useMemo, useState } from "react";
import { KeyRoundIcon, LogInIcon } from "lucide-react";
import { AuthSession } from "../types/crm";

interface AuthScreenProps {
  initialSession: AuthSession;
  error?: string | null;
  loading?: boolean;
  onLogin: (session: AuthSession) => void;
  onSignup: (payload: {
    baseUrl: string;
    companyName: string;
    tenantId: string;
    fullName: string;
    email: string;
    password: string;
  }) => void;
}

type AuthMode = "login" | "signup";

function slugifyTenant(value: string) {
  return value
    .trim()
    .toLowerCase()
    .replace(/[^a-z0-9]+/g, "-")
    .replace(/^-+|-+$/g, "")
    .slice(0, 100);
}

export function AuthScreen({
  initialSession,
  error,
  loading = false,
  onLogin,
  onSignup,
}: AuthScreenProps) {
  const [mode, setMode] = useState<AuthMode>("login");
  const [loginForm, setLoginForm] = useState<AuthSession>(initialSession);
  const [signupForm, setSignupForm] = useState({
    baseUrl: initialSession.baseUrl,
    companyName: "",
    tenantId: "",
    fullName: "",
    email: "",
    password: "",
    confirmPassword: "",
  });

  const tenantSuggestion = useMemo(
    () => slugifyTenant(signupForm.companyName),
    [signupForm.companyName],
  );
  const effectiveSignupTenantId = (signupForm.tenantId.trim() || tenantSuggestion).toLowerCase();

  const updateLoginField =
    (field: keyof AuthSession) =>
    (event: React.ChangeEvent<HTMLInputElement>) => {
      setLoginForm((current) => ({ ...current, [field]: event.target.value }));
    };

  const updateSignupField =
    (field: keyof typeof signupForm) =>
    (event: React.ChangeEvent<HTMLInputElement>) => {
      setSignupForm((current) => ({
        ...current,
        [field]: event.target.value,
      }));
    };

  const handleLogin = (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    onLogin({
      ...loginForm,
      baseUrl: loginForm.baseUrl.trim().replace(/\/$/, ""),
      tenantId: loginForm.tenantId.trim().toLowerCase(),
      username: loginForm.username.trim().toLowerCase(),
      password: loginForm.password ?? "",
    });
  };

  const handleSignup = (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    if (signupForm.password !== signupForm.confirmPassword) {
      return;
    }

    onSignup({
      baseUrl: signupForm.baseUrl.trim().replace(/\/$/, ""),
      companyName: signupForm.companyName.trim(),
      tenantId: effectiveSignupTenantId,
      fullName: signupForm.fullName.trim(),
      email: signupForm.email.trim().toLowerCase(),
      password: signupForm.password,
    });
  };

  const signupPasswordMismatch =
    signupForm.confirmPassword.length > 0 &&
    signupForm.password !== signupForm.confirmPassword;

  const inputClassName =
    "w-full rounded-lg border border-gray-200 bg-gray-50 px-3 py-2 text-sm text-gray-900 outline-none transition focus:border-gray-400 focus:bg-white";

  return (
    <div className="flex min-h-screen items-center justify-center bg-[#f8f9fa] px-4 py-6 sm:px-6">
      <div className="w-full max-w-[340px] rounded-2xl border border-gray-200 bg-white p-5 shadow-xl shadow-gray-200/60 sm:p-6">
        {/* Logo & title */}
        <div className="mb-5 flex flex-col items-center text-center">
          <div className="mb-2 flex h-9 w-9 items-center justify-center rounded-lg bg-gray-900 text-sm font-bold text-white">
            A
          </div>
          <h1 className="text-lg font-semibold text-gray-900">Alad CRM</h1>
          <p className="mt-0.5 text-xs text-gray-500">
            {mode === "login"
              ? "Sign in to your workspace"
              : "Create a new workspace"}
          </p>
        </div>

        {/* Login / Signup toggle */}
        <div className="mb-4 flex rounded-lg border border-gray-200 bg-gray-50 p-0.5">
          <button
            type="button"
            onClick={() => setMode("login")}
            className={`flex-1 rounded-md px-3 py-1.5 text-xs font-medium transition-colors ${
              mode === "login" ? "bg-white text-gray-900 shadow-sm" : "text-gray-500 hover:text-gray-900"
            }`}
          >
            Log in
          </button>
          <button
            type="button"
            onClick={() => setMode("signup")}
            className={`flex-1 rounded-md px-3 py-1.5 text-xs font-medium transition-colors ${
              mode === "signup" ? "bg-white text-gray-900 shadow-sm" : "text-gray-500 hover:text-gray-900"
            }`}
          >
            Sign up
          </button>
        </div>

        {/* Login form */}
        {mode === "login" ? (
          <form onSubmit={handleLogin} className="space-y-4">
            <label className="block">
              <span className="mb-1 block text-xs font-medium text-gray-600">Workspace ID</span>
              <input
                value={loginForm.tenantId}
                onChange={updateLoginField("tenantId")}
                className={inputClassName}
                placeholder="your-workspace"
              />
            </label>

            <label className="block">
              <span className="mb-1 block text-xs font-medium text-gray-600">Email</span>
              <input
                value={loginForm.username}
                onChange={updateLoginField("username")}
                className={inputClassName}
                placeholder="you@company.com"
              />
            </label>

            <label className="block">
              <span className="mb-1 block text-xs font-medium text-gray-600">Password</span>
              <input
                type="password"
                value={loginForm.password ?? ""}
                onChange={updateLoginField("password")}
                className={inputClassName}
                placeholder="Enter your password"
              />
            </label>

            {error ? (
              <div className="rounded-lg border border-red-200 bg-red-50 px-3 py-2 text-xs text-red-700">
                {error}
              </div>
            ) : null}

            <button
              type="submit"
              disabled={loading}
              className="flex w-full items-center justify-center gap-2 rounded-lg bg-black px-4 py-2.5 text-sm font-medium text-white transition-colors hover:bg-gray-800 disabled:cursor-not-allowed disabled:bg-gray-400"
            >
              <LogInIcon className="h-4 w-4" />
              {loading ? "Signing in..." : "Sign in"}
            </button>
          </form>
        ) : (
          /* Signup form */
          <form onSubmit={handleSignup} className="space-y-3.5">
            <div className="space-y-3.5">
              <label className="block">
                <span className="mb-1 block text-xs font-medium text-gray-600">Company name</span>
                <input
                  value={signupForm.companyName}
                  onChange={updateSignupField("companyName")}
                  className={inputClassName}
                  placeholder="Acme Inc"
                />
              </label>

              <label className="block">
                <span className="mb-1 block text-xs font-medium text-gray-600">Workspace ID</span>
                <input
                  value={signupForm.tenantId}
                  onChange={updateSignupField("tenantId")}
                  className={inputClassName}
                  placeholder={tenantSuggestion || "acme-inc"}
                />
              </label>

              <label className="block">
                <span className="mb-1 block text-xs font-medium text-gray-600">Full name</span>
                <input
                  value={signupForm.fullName}
                  onChange={updateSignupField("fullName")}
                  className={inputClassName}
                  placeholder="Jane Doe"
                />
              </label>

              <label className="block">
                <span className="mb-1 block text-xs font-medium text-gray-600">Email</span>
                <input
                  value={signupForm.email}
                  onChange={updateSignupField("email")}
                  className={inputClassName}
                  placeholder="jane@company.com"
                />
              </label>

              <label className="block">
                <span className="mb-1 block text-xs font-medium text-gray-600">Password</span>
                <input
                  type="password"
                  value={signupForm.password}
                  onChange={updateSignupField("password")}
                  className={inputClassName}
                  placeholder="At least 8 characters"
                />
              </label>

              <label className="block">
                <span className="mb-1 block text-xs font-medium text-gray-600">Confirm password</span>
                <input
                  type="password"
                  value={signupForm.confirmPassword}
                  onChange={updateSignupField("confirmPassword")}
                  className={inputClassName}
                  placeholder="Re-enter password"
                />
              </label>
            </div>

            {error ? (
              <div className="rounded-lg border border-red-200 bg-red-50 px-3 py-2 text-xs text-red-700">
                {error}
              </div>
            ) : signupPasswordMismatch ? (
              <div className="rounded-lg border border-red-200 bg-red-50 px-3 py-2 text-xs text-red-700">
                Password confirmation does not match.
              </div>
            ) : null}

            <button
              type="submit"
              disabled={
                loading ||
                signupPasswordMismatch ||
                !signupForm.companyName ||
                !effectiveSignupTenantId ||
                !signupForm.fullName ||
                !signupForm.email ||
                !signupForm.password
              }
              className="flex w-full items-center justify-center gap-2 rounded-lg bg-black px-4 py-2.5 text-sm font-medium text-white transition-colors hover:bg-gray-800 disabled:cursor-not-allowed disabled:bg-gray-400"
            >
              <KeyRoundIcon className="h-4 w-4" />
              {loading ? "Creating workspace..." : "Create workspace"}
            </button>
          </form>
        )}
      </div>
    </div>
  );
}
