import React, { FormEvent, useMemo, useState } from "react";
import {
  Building2Icon,
  KeyRoundIcon,
  LockIcon,
  LogInIcon,
  ShieldCheckIcon,
  SparklesIcon,
  UserCircle2Icon,
} from "lucide-react";
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

const presets = [
  {
    label: "Developer workspace",
    session: {
      baseUrl: "http://localhost:8080",
      tenantId: "tenant-demo",
      username: "local-dev",
      password: "local-dev-pass",
    },
  },
  {
    label: "Viewer workspace",
    session: {
      baseUrl: "http://localhost:8080",
      tenantId: "tenant-demo",
      username: "local-view",
      password: "local-view-pass",
    },
  },
];

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
        tenantId:
          field === "companyName" && !current.tenantId
            ? slugifyTenant(event.target.value)
            : current.tenantId,
      }));
    };

  const handleLogin = (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    onLogin({
      ...loginForm,
      baseUrl: loginForm.baseUrl.trim().replace(/\/$/, ""),
      tenantId: loginForm.tenantId.trim().toLowerCase(),
      username: loginForm.username.trim().toLowerCase(),
      password: loginForm.password,
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
      tenantId: signupForm.tenantId.trim().toLowerCase(),
      fullName: signupForm.fullName.trim(),
      email: signupForm.email.trim().toLowerCase(),
      password: signupForm.password,
    });
  };

  const signupPasswordMismatch =
    signupForm.confirmPassword.length > 0 &&
    signupForm.password !== signupForm.confirmPassword;

  return (
    <div className="min-h-screen bg-[#f8f9fa] px-4 py-8 sm:px-6 lg:px-8">
      <div className="mx-auto grid min-h-[calc(100vh-4rem)] w-full max-w-6xl overflow-hidden rounded-[28px] border border-gray-200 bg-white shadow-xl shadow-gray-200/60 lg:grid-cols-[1.02fr_0.98fr]">
        <div className="border-b border-gray-200 bg-[#fcfcfc] p-8 lg:border-b-0 lg:border-r lg:p-12">
          <div className="flex items-center gap-3">
            <div className="flex h-12 w-12 items-center justify-center rounded-2xl bg-pink-500 text-white">
              <ShieldCheckIcon className="h-6 w-6" />
            </div>
            <div>
              <div className="text-lg font-semibold text-gray-900">AI CRM</div>
              <div className="text-sm text-gray-500">Workspace access</div>
            </div>
          </div>

          <h1 className="mt-10 text-4xl font-semibold leading-tight text-gray-900">
            Tenant-aware access for every customer workspace.
          </h1>
          <p className="mt-4 max-w-xl text-base leading-8 text-gray-600">
            Companies can register their own workspace, choose a tenant identifier, and onboard an admin user whose name
            flows through the platform. Existing workspaces can sign in with workspace ID, work email, and password.
          </p>

          <div className="mt-10 grid gap-4 sm:grid-cols-2">
            <div className="rounded-2xl border border-gray-200 bg-white p-5">
              <div className="mb-3 flex items-center gap-2 text-sm font-medium text-gray-700">
                <Building2Icon className="h-4 w-4 text-gray-400" />
                Workspace identity
              </div>
              <div className="space-y-2 text-sm text-gray-600">
                <div>Each company gets its own tenant identifier such as `northwind-services`.</div>
                <div>That workspace ID becomes the tenant scope used across the platform.</div>
              </div>
            </div>

            <div className="rounded-2xl border border-gray-200 bg-white p-5">
              <div className="mb-3 flex items-center gap-2 text-sm font-medium text-gray-700">
                <UserCircle2Icon className="h-4 w-4 text-gray-400" />
                Named operators
              </div>
              <div className="space-y-2 text-sm text-gray-600">
                <div>The admin name captured during signup becomes the user identity shown in the shell.</div>
                <div>That same name can now flow into audit activity for future actions.</div>
              </div>
            </div>
          </div>

          <div className="mt-8 rounded-2xl border border-dashed border-gray-200 bg-white p-5">
            <div className="mb-3 flex items-center gap-2 text-sm font-medium text-gray-700">
              <SparklesIcon className="h-4 w-4 text-gray-400" />
              Local test shortcuts
            </div>
            <div className="flex flex-wrap gap-3">
              {presets.map((preset) => (
                <button
                  key={preset.label}
                  type="button"
                  onClick={() => {
                    setMode("login");
                    setLoginForm(preset.session);
                  }}
                  className="rounded-xl border border-gray-200 bg-white px-4 py-3 text-left text-sm text-gray-700 transition hover:bg-gray-50"
                >
                  <div className="font-medium text-gray-900">{preset.label}</div>
                  <div className="text-xs text-gray-500">
                    {preset.session.tenantId} · {preset.session.username}
                  </div>
                </button>
              ))}
            </div>
          </div>
        </div>

        <div className="flex items-center p-8 lg:p-12">
          <div className="w-full">
            <div className="mb-8 flex items-center gap-3">
              <div className="flex h-10 w-10 items-center justify-center rounded-full bg-gray-100 text-gray-500">
                <LockIcon className="h-4 w-4" />
              </div>
              <div>
                <div className="text-lg font-semibold text-gray-900">Authentication</div>
                <div className="text-sm text-gray-500">Sign in to an existing workspace or create a new tenant</div>
              </div>
            </div>

            <div className="mb-6 flex rounded-xl border border-gray-200 bg-gray-50 p-1">
              <button
                type="button"
                onClick={() => setMode("login")}
                className={`flex-1 rounded-lg px-4 py-2 text-sm font-medium transition-colors ${
                  mode === "login" ? "bg-white text-gray-900 shadow-sm" : "text-gray-500 hover:text-gray-900"
                }`}
              >
                Log in
              </button>
              <button
                type="button"
                onClick={() => {
                  setMode("signup");
                  setSignupForm((current) => ({
                    ...current,
                    tenantId: current.tenantId || tenantSuggestion,
                  }));
                }}
                className={`flex-1 rounded-lg px-4 py-2 text-sm font-medium transition-colors ${
                  mode === "signup" ? "bg-white text-gray-900 shadow-sm" : "text-gray-500 hover:text-gray-900"
                }`}
              >
                Sign up
              </button>
            </div>

            {mode === "login" ? (
              <form onSubmit={handleLogin} className="space-y-5">
                <label className="block">
                  <span className="mb-2 block text-sm font-medium text-gray-700">Backend URL</span>
                  <input
                    value={loginForm.baseUrl}
                    onChange={updateLoginField("baseUrl")}
                    className="w-full rounded-xl border border-gray-200 bg-gray-50 px-4 py-3 text-sm text-gray-900 outline-none transition focus:border-gray-400 focus:bg-white"
                    placeholder="http://localhost:8080"
                  />
                </label>

                <div className="grid gap-5 sm:grid-cols-2">
                  <label className="block">
                    <span className="mb-2 block text-sm font-medium text-gray-700">Workspace ID</span>
                    <input
                      value={loginForm.tenantId}
                      onChange={updateLoginField("tenantId")}
                      className="w-full rounded-xl border border-gray-200 bg-gray-50 px-4 py-3 text-sm text-gray-900 outline-none transition focus:border-gray-400 focus:bg-white"
                      placeholder="tenant-demo"
                    />
                  </label>

                  <label className="block">
                    <span className="mb-2 block text-sm font-medium text-gray-700">Work email</span>
                    <input
                      value={loginForm.username}
                      onChange={updateLoginField("username")}
                      className="w-full rounded-xl border border-gray-200 bg-gray-50 px-4 py-3 text-sm text-gray-900 outline-none transition focus:border-gray-400 focus:bg-white"
                      placeholder="you@company.com"
                    />
                  </label>
                </div>

                <label className="block">
                  <span className="mb-2 block text-sm font-medium text-gray-700">Password</span>
                  <input
                    type="password"
                    value={loginForm.password}
                    onChange={updateLoginField("password")}
                    className="w-full rounded-xl border border-gray-200 bg-gray-50 px-4 py-3 text-sm text-gray-900 outline-none transition focus:border-gray-400 focus:bg-white"
                    placeholder="Enter your password"
                  />
                </label>

                {error ? (
                  <div className="rounded-xl border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">
                    {error}
                  </div>
                ) : (
                  <div className="rounded-xl border border-gray-200 bg-gray-50 px-4 py-3 text-sm text-gray-600">
                    Login validates the workspace first, then stores the local session so you can continue testing flows.
                  </div>
                )}

                <button
                  type="submit"
                  disabled={loading}
                  className="flex w-full items-center justify-center gap-2 rounded-xl bg-black px-4 py-3 text-sm font-medium text-white transition-colors hover:bg-gray-800 disabled:cursor-not-allowed disabled:bg-gray-400"
                >
                  <LogInIcon className="h-4 w-4" />
                  {loading ? "Signing in..." : "Sign in to workspace"}
                </button>
              </form>
            ) : (
              <form onSubmit={handleSignup} className="space-y-5">
                <label className="block">
                  <span className="mb-2 block text-sm font-medium text-gray-700">Backend URL</span>
                  <input
                    value={signupForm.baseUrl}
                    onChange={updateSignupField("baseUrl")}
                    className="w-full rounded-xl border border-gray-200 bg-gray-50 px-4 py-3 text-sm text-gray-900 outline-none transition focus:border-gray-400 focus:bg-white"
                    placeholder="http://localhost:8080"
                  />
                </label>

                <div className="grid gap-5 sm:grid-cols-2">
                  <label className="block">
                    <span className="mb-2 block text-sm font-medium text-gray-700">Company name</span>
                    <input
                      value={signupForm.companyName}
                      onChange={updateSignupField("companyName")}
                      className="w-full rounded-xl border border-gray-200 bg-gray-50 px-4 py-3 text-sm text-gray-900 outline-none transition focus:border-gray-400 focus:bg-white"
                      placeholder="Northwind Services"
                    />
                  </label>

                  <label className="block">
                    <span className="mb-2 block text-sm font-medium text-gray-700">Workspace ID</span>
                    <input
                      value={signupForm.tenantId}
                      onChange={updateSignupField("tenantId")}
                      className="w-full rounded-xl border border-gray-200 bg-gray-50 px-4 py-3 text-sm text-gray-900 outline-none transition focus:border-gray-400 focus:bg-white"
                      placeholder={tenantSuggestion || "northwind-services"}
                    />
                    <span className="mt-2 block text-xs text-gray-500">
                      Suggested from company name: {tenantSuggestion || "choose-a-workspace-id"}
                    </span>
                  </label>
                </div>

                <div className="grid gap-5 sm:grid-cols-2">
                  <label className="block">
                    <span className="mb-2 block text-sm font-medium text-gray-700">Admin full name</span>
                    <input
                      value={signupForm.fullName}
                      onChange={updateSignupField("fullName")}
                      className="w-full rounded-xl border border-gray-200 bg-gray-50 px-4 py-3 text-sm text-gray-900 outline-none transition focus:border-gray-400 focus:bg-white"
                      placeholder="Takudzwa Chitsungo"
                    />
                  </label>

                  <label className="block">
                    <span className="mb-2 block text-sm font-medium text-gray-700">Admin work email</span>
                    <input
                      value={signupForm.email}
                      onChange={updateSignupField("email")}
                      className="w-full rounded-xl border border-gray-200 bg-gray-50 px-4 py-3 text-sm text-gray-900 outline-none transition focus:border-gray-400 focus:bg-white"
                      placeholder="takudzwa@company.com"
                    />
                  </label>
                </div>

                <div className="grid gap-5 sm:grid-cols-2">
                  <label className="block">
                    <span className="mb-2 block text-sm font-medium text-gray-700">Password</span>
                    <input
                      type="password"
                      value={signupForm.password}
                      onChange={updateSignupField("password")}
                      className="w-full rounded-xl border border-gray-200 bg-gray-50 px-4 py-3 text-sm text-gray-900 outline-none transition focus:border-gray-400 focus:bg-white"
                      placeholder="At least 8 characters"
                    />
                  </label>

                  <label className="block">
                    <span className="mb-2 block text-sm font-medium text-gray-700">Confirm password</span>
                    <input
                      type="password"
                      value={signupForm.confirmPassword}
                      onChange={updateSignupField("confirmPassword")}
                      className="w-full rounded-xl border border-gray-200 bg-gray-50 px-4 py-3 text-sm text-gray-900 outline-none transition focus:border-gray-400 focus:bg-white"
                      placeholder="Re-enter password"
                    />
                  </label>
                </div>

                {error ? (
                  <div className="rounded-xl border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">
                    {error}
                  </div>
                ) : signupPasswordMismatch ? (
                  <div className="rounded-xl border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">
                    Password confirmation does not match.
                  </div>
                ) : (
                  <div className="rounded-xl border border-gray-200 bg-gray-50 px-4 py-3 text-sm text-gray-600">
                    Signup creates the company workspace, stores the tenant profile, and provisions the first admin user.
                  </div>
                )}

                <button
                  type="submit"
                  disabled={
                    loading ||
                    signupPasswordMismatch ||
                    !signupForm.companyName ||
                    !signupForm.tenantId ||
                    !signupForm.fullName ||
                    !signupForm.email ||
                    !signupForm.password
                  }
                  className="flex w-full items-center justify-center gap-2 rounded-xl bg-black px-4 py-3 text-sm font-medium text-white transition-colors hover:bg-gray-800 disabled:cursor-not-allowed disabled:bg-gray-400"
                >
                  <KeyRoundIcon className="h-4 w-4" />
                  {loading ? "Creating workspace..." : "Create workspace"}
                </button>
              </form>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}
