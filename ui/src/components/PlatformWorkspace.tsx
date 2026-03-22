import React, { useMemo, useState } from "react";
import {
  changeCurrentPassword,
  createCustomEntityDefinition,
  createWorkspaceUser,
  createWorkflowDefinition,
  installMarketplaceApp,
  loadCustomEntityRecords,
  resetWorkspaceUserPassword,
  updateWorkspaceUser,
} from "../lib/api";
import {
  AppUserRecord,
  AuthSession,
  CustomEntityDefinitionRecord,
  CustomEntityRecordItem,
  IntegrationConnectionRecord,
  IntegrationMarketplaceAppRecord,
  WorkflowBuilderCatalog,
  WorkflowDefinitionRecord,
} from "../types/crm";

interface PlatformWorkspaceProps {
  session: AuthSession | null;
  customEntityDefinitions?: CustomEntityDefinitionRecord[];
  users?: AppUserRecord[];
  workflowDefinitions?: WorkflowDefinitionRecord[];
  workflowCatalog?: WorkflowBuilderCatalog | null;
  integrations?: IntegrationConnectionRecord[];
  marketplaceApps?: IntegrationMarketplaceAppRecord[];
  onRefresh: () => Promise<void>;
}

function formatDate(value?: string | null) {
  if (!value) {
    return "N/A";
  }
  return new Date(value).toLocaleString();
}

export function PlatformWorkspace({
  session,
  customEntityDefinitions = [],
  users = [],
  workflowDefinitions = [],
  workflowCatalog,
  integrations = [],
  marketplaceApps = [],
  onRefresh,
}: PlatformWorkspaceProps) {
  const [customEntityError, setCustomEntityError] = useState<string | null>(null);
  const [workflowError, setWorkflowError] = useState<string | null>(null);
  const [marketplaceError, setMarketplaceError] = useState<string | null>(null);
  const [recordError, setRecordError] = useState<string | null>(null);
  const [userError, setUserError] = useState<string | null>(null);
  const [passwordError, setPasswordError] = useState<string | null>(null);
  const [passwordSuccess, setPasswordSuccess] = useState<string | null>(null);
  const [userActionPendingId, setUserActionPendingId] = useState<number | null>(null);
  const [loadingRecordsFor, setLoadingRecordsFor] = useState<number | null>(null);
  const [selectedDefinitionId, setSelectedDefinitionId] = useState<number | null>(null);
  const [selectedDefinitionRecords, setSelectedDefinitionRecords] = useState<CustomEntityRecordItem[]>([]);

  const [customEntityForm, setCustomEntityForm] = useState({
    name: "",
    apiName: "",
    pluralLabel: "",
    fieldSchemaJson: '{ "serialNumber": { "type": "TEXT" }, "region": { "type": "TEXT" } }',
  });
  const [userForm, setUserForm] = useState({
    fullName: "",
    email: "",
    password: "",
    role: "VIEWER",
  });
  const [userDrafts, setUserDrafts] = useState<Record<number, { role: string; active: boolean }>>({});
  const [userPasswords, setUserPasswords] = useState<Record<number, string>>({});
  const [passwordForm, setPasswordForm] = useState({
    currentPassword: "",
    newPassword: "",
    confirmPassword: "",
  });
  const [workflowForm, setWorkflowForm] = useState({
    name: "",
    triggerType: workflowCatalog?.triggerTypes?.[0] ?? "LEAD_CREATED",
    targetEntityType: workflowCatalog?.targetEntityTypes?.[0] ?? "LEAD",
    targetEntityApiName: "",
    actionType: workflowCatalog?.actionTypes?.[0] ?? "CREATE_ACTIVITY",
    actionSubject: "",
    actionDetails: "",
  });

  const marketplaceSummary = useMemo(() => {
    const installedKeys = new Set(
      integrations
        .map((connection) => connection.marketplaceAppKey)
        .filter((value): value is string => Boolean(value)),
    );
    return marketplaceApps.map((app) => ({
      ...app,
      installed: installedKeys.has(app.appKey),
    }));
  }, [integrations, marketplaceApps]);

  const openRecords = async (definitionId: number) => {
    if (!session) {
      return;
    }
    setRecordError(null);
    setLoadingRecordsFor(definitionId);
    try {
      const records = await loadCustomEntityRecords(session, definitionId);
      setSelectedDefinitionId(definitionId);
      setSelectedDefinitionRecords(records);
    } catch (error) {
      setRecordError(error instanceof Error ? error.message : "Unable to load custom entity records.");
    } finally {
      setLoadingRecordsFor(null);
    }
  };

  const submitCustomEntity = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    if (!session) {
      return;
    }
    setCustomEntityError(null);
    try {
      await createCustomEntityDefinition(session, {
        name: customEntityForm.name,
        apiName: customEntityForm.apiName,
        pluralLabel: customEntityForm.pluralLabel || undefined,
        fieldSchemaJson: customEntityForm.fieldSchemaJson,
        active: true,
      });
      setCustomEntityForm({
        name: "",
        apiName: "",
        pluralLabel: "",
        fieldSchemaJson: '{ "serialNumber": { "type": "TEXT" }, "region": { "type": "TEXT" } }',
      });
      await onRefresh();
    } catch (error) {
      setCustomEntityError(error instanceof Error ? error.message : "Unable to create custom entity.");
    }
  };

  const submitWorkflow = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    if (!session) {
      return;
    }
    setWorkflowError(null);
    try {
      await createWorkflowDefinition(session, {
        name: workflowForm.name,
        triggerType: workflowForm.triggerType,
        targetEntityType: workflowForm.targetEntityType,
        targetEntityApiName:
          workflowForm.targetEntityType === "CUSTOM_ENTITY" ? workflowForm.targetEntityApiName : undefined,
        actionType: workflowForm.actionType,
        actionSubject: workflowForm.actionSubject,
        actionDetails: workflowForm.actionDetails || undefined,
        active: true,
      });
      setWorkflowForm({
        name: "",
        triggerType: workflowCatalog?.triggerTypes?.[0] ?? "LEAD_CREATED",
        targetEntityType: workflowCatalog?.targetEntityTypes?.[0] ?? "LEAD",
        targetEntityApiName: "",
        actionType: workflowCatalog?.actionTypes?.[0] ?? "CREATE_ACTIVITY",
        actionSubject: "",
        actionDetails: "",
      });
      await onRefresh();
    } catch (error) {
      setWorkflowError(error instanceof Error ? error.message : "Unable to create workflow.");
    }
  };

  const installApp = async (appKey: string, connectionName: string) => {
    if (!session) {
      return;
    }
    setMarketplaceError(null);
    try {
      await installMarketplaceApp(session, { appKey, connectionName });
      await onRefresh();
    } catch (error) {
      setMarketplaceError(error instanceof Error ? error.message : "Unable to install marketplace app.");
    }
  };

  const submitUser = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    if (!session) {
      return;
    }
    setUserError(null);
    try {
      await createWorkspaceUser(session, userForm);
      setUserForm({
        fullName: "",
        email: "",
        password: "",
        role: "VIEWER",
      });
      await onRefresh();
    } catch (error) {
      setUserError(error instanceof Error ? error.message : "Unable to create workspace user.");
    }
  };

  const currentUserDraft = (user: AppUserRecord) =>
    userDrafts[user.id] ?? { role: user.role, active: user.active };

  const saveUser = async (user: AppUserRecord) => {
    if (!session) {
      return;
    }
    setUserError(null);
    setUserActionPendingId(user.id);
    try {
      const draft = currentUserDraft(user);
      await updateWorkspaceUser(session, user.id, draft);
      await onRefresh();
    } catch (error) {
      setUserError(error instanceof Error ? error.message : "Unable to update workspace user.");
    } finally {
      setUserActionPendingId(null);
    }
  };

  const resetUserPassword = async (user: AppUserRecord) => {
    if (!session) {
      return;
    }
    const nextPassword = userPasswords[user.id]?.trim();
    if (!nextPassword) {
      setUserError("Enter a new password before saving a reset.");
      return;
    }
    setUserError(null);
    setUserActionPendingId(user.id);
    try {
      await resetWorkspaceUserPassword(session, user.id, { password: nextPassword });
      setUserPasswords((current) => {
        const copy = { ...current };
        delete copy[user.id];
        return copy;
      });
      setPasswordSuccess(`Password reset for ${user.fullName}.`);
    } catch (error) {
      setUserError(error instanceof Error ? error.message : "Unable to reset workspace user password.");
    } finally {
      setUserActionPendingId(null);
    }
  };

  const submitPasswordChange = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    if (!session) {
      return;
    }
    if (passwordForm.newPassword !== passwordForm.confirmPassword) {
      setPasswordError("New password and confirmation do not match.");
      setPasswordSuccess(null);
      return;
    }
    setPasswordError(null);
    setPasswordSuccess(null);
    try {
      const response = await changeCurrentPassword(session, {
        currentPassword: passwordForm.currentPassword,
        newPassword: passwordForm.newPassword,
      });
      setPasswordForm({
        currentPassword: "",
        newPassword: "",
        confirmPassword: "",
      });
      setPasswordSuccess(response.message);
    } catch (error) {
      setPasswordError(error instanceof Error ? error.message : "Unable to update your password.");
    }
  };

  return (
    <div className="flex h-full min-h-0 flex-col bg-[#f8f9fa]">
      <div className="border-b border-gray-200 bg-white px-6 py-5">
        <h1 className="text-xl font-semibold text-gray-900">Platform Extensions</h1>
        <p className="mt-1 text-sm text-gray-500">
          Manage custom entities, workflow builder metadata, and installable marketplace integrations.
        </p>
      </div>

      <div className="grid min-h-0 flex-1 grid-cols-1 gap-6 overflow-y-auto p-6 xl:grid-cols-[1.2fr_1fr]">
        <div className="space-y-6">
          <section className="rounded-xl border border-gray-200 bg-white p-5">
            <div className="mb-4 flex items-center justify-between">
              <div>
                <h2 className="text-base font-semibold text-gray-900">Workspace Security</h2>
                <p className="text-sm text-gray-500">Change your own password without leaving the platform workspace.</p>
              </div>
            </div>

            <form onSubmit={submitPasswordChange} className="grid gap-3 md:grid-cols-3">
              <input
                type="password"
                value={passwordForm.currentPassword}
                onChange={(event) => setPasswordForm((current) => ({ ...current, currentPassword: event.target.value }))}
                placeholder="Current password"
                className="rounded-md border border-gray-200 px-3 py-2 text-sm"
              />
              <input
                type="password"
                value={passwordForm.newPassword}
                onChange={(event) => setPasswordForm((current) => ({ ...current, newPassword: event.target.value }))}
                placeholder="New password"
                className="rounded-md border border-gray-200 px-3 py-2 text-sm"
              />
              <input
                type="password"
                value={passwordForm.confirmPassword}
                onChange={(event) => setPasswordForm((current) => ({ ...current, confirmPassword: event.target.value }))}
                placeholder="Confirm password"
                className="rounded-md border border-gray-200 px-3 py-2 text-sm"
              />
              <div className="md:col-span-3 flex items-center justify-between gap-3">
                <div className={`text-xs ${passwordError ? "text-rose-500" : "text-emerald-600"}`}>
                  {passwordError ?? passwordSuccess}
                </div>
                <button className="rounded-md bg-gray-900 px-4 py-2 text-sm font-medium text-white hover:bg-gray-800">
                  Update my password
                </button>
              </div>
            </form>
          </section>

          <section className="rounded-xl border border-gray-200 bg-white p-5">
            <div className="mb-4 flex items-center justify-between">
              <div>
                <h2 className="text-base font-semibold text-gray-900">Workspace Users</h2>
                <p className="text-sm text-gray-500">Provision tenant-scoped admins and viewers for the current workspace.</p>
              </div>
              <div className="rounded-full bg-gray-100 px-3 py-1 text-xs font-medium text-gray-600">
                {users.length} users
              </div>
            </div>

            <form onSubmit={submitUser} className="mb-5 grid gap-3 md:grid-cols-2">
              <input
                value={userForm.fullName}
                onChange={(event) => setUserForm((current) => ({ ...current, fullName: event.target.value }))}
                placeholder="Full name"
                className="rounded-md border border-gray-200 px-3 py-2 text-sm"
              />
              <input
                value={userForm.email}
                onChange={(event) => setUserForm((current) => ({ ...current, email: event.target.value }))}
                placeholder="Email address"
                className="rounded-md border border-gray-200 px-3 py-2 text-sm"
              />
              <input
                type="password"
                value={userForm.password}
                onChange={(event) => setUserForm((current) => ({ ...current, password: event.target.value }))}
                placeholder="Temporary password"
                className="rounded-md border border-gray-200 px-3 py-2 text-sm"
              />
              <select
                value={userForm.role}
                onChange={(event) => setUserForm((current) => ({ ...current, role: event.target.value }))}
                className="rounded-md border border-gray-200 px-3 py-2 text-sm"
              >
                <option value="VIEWER">Viewer</option>
                <option value="ADMIN">Admin</option>
              </select>
              <div className="md:col-span-2 flex items-center justify-between gap-3">
                <div className="text-xs text-rose-500">{userError}</div>
                <button className="rounded-md bg-gray-900 px-4 py-2 text-sm font-medium text-white hover:bg-gray-800">
                  Add user
                </button>
              </div>
            </form>

            <div className="space-y-3">
              {users.length === 0 ? (
                <div className="rounded-lg border border-dashed border-gray-200 px-4 py-6 text-sm text-gray-500">
                  No workspace users provisioned yet.
                </div>
              ) : (
                users.map((user) => (
                  <div key={user.id} className="rounded-lg border border-gray-200 px-4 py-4">
                    <div className="flex flex-wrap items-start justify-between gap-3">
                      <div>
                        <div className="text-sm font-semibold text-gray-900">{user.fullName}</div>
                        <div className="mt-1 text-xs text-gray-500">
                          {user.email} · {user.role} · {user.active ? "Active" : "Inactive"}
                        </div>
                        <div className="mt-3 max-w-xs">
                          <input
                            type="password"
                            value={userPasswords[user.id] ?? ""}
                            onChange={(event) =>
                              setUserPasswords((current) => ({
                                ...current,
                                [user.id]: event.target.value,
                              }))
                            }
                            placeholder="Reset password"
                            className="w-full rounded-md border border-gray-200 px-2 py-1 text-xs"
                          />
                        </div>
                      </div>
                      <div className="flex flex-col items-end gap-2">
                        <div className="text-xs text-gray-400">{formatDate(user.createdAt)}</div>
                        <div className="flex flex-wrap items-center justify-end gap-2">
                          <select
                            value={currentUserDraft(user).role}
                            onChange={(event) =>
                              setUserDrafts((current) => ({
                                ...current,
                                [user.id]: {
                                  ...currentUserDraft(user),
                                  role: event.target.value,
                                },
                              }))
                            }
                            className="rounded-md border border-gray-200 px-2 py-1 text-xs"
                          >
                            <option value="VIEWER">Viewer</option>
                            <option value="ADMIN">Admin</option>
                          </select>
                          <button
                            onClick={() =>
                              setUserDrafts((current) => ({
                                ...current,
                                [user.id]: {
                                  ...currentUserDraft(user),
                                  active: !currentUserDraft(user).active,
                                },
                              }))
                            }
                            className={`rounded-md px-2 py-1 text-xs font-medium ${
                              currentUserDraft(user).active
                                ? "border border-green-200 bg-green-50 text-green-700"
                                : "border border-gray-200 bg-gray-50 text-gray-600"
                            }`}
                          >
                            {currentUserDraft(user).active ? "Active" : "Inactive"}
                          </button>
                          <button
                            onClick={() => void saveUser(user)}
                            disabled={userActionPendingId === user.id}
                            className="rounded-md bg-gray-900 px-3 py-1 text-xs font-medium text-white hover:bg-gray-800 disabled:bg-gray-400"
                          >
                            {userActionPendingId === user.id ? "Saving..." : "Save"}
                          </button>
                          <button
                            onClick={() => void resetUserPassword(user)}
                            disabled={userActionPendingId === user.id}
                            className="rounded-md border border-gray-200 px-3 py-1 text-xs font-medium text-gray-700 hover:bg-gray-50 disabled:bg-gray-100"
                          >
                            Reset password
                          </button>
                        </div>
                      </div>
                    </div>
                  </div>
                ))
              )}
            </div>
          </section>

          <section className="rounded-xl border border-gray-200 bg-white p-5">
            <div className="mb-4 flex items-center justify-between">
              <div>
                <h2 className="text-base font-semibold text-gray-900">Custom Entities</h2>
                <p className="text-sm text-gray-500">Create tenant extensions with field schemas the platform can validate.</p>
              </div>
              <div className="rounded-full bg-gray-100 px-3 py-1 text-xs font-medium text-gray-600">
                {customEntityDefinitions.length} definitions
              </div>
            </div>

            <form onSubmit={submitCustomEntity} className="mb-5 grid gap-3 md:grid-cols-2">
              <input
                value={customEntityForm.name}
                onChange={(event) => setCustomEntityForm((current) => ({ ...current, name: event.target.value }))}
                placeholder="Entity name"
                className="rounded-md border border-gray-200 px-3 py-2 text-sm"
              />
              <input
                value={customEntityForm.apiName}
                onChange={(event) => setCustomEntityForm((current) => ({ ...current, apiName: event.target.value }))}
                placeholder="api_name"
                className="rounded-md border border-gray-200 px-3 py-2 text-sm"
              />
              <input
                value={customEntityForm.pluralLabel}
                onChange={(event) => setCustomEntityForm((current) => ({ ...current, pluralLabel: event.target.value }))}
                placeholder="Plural label"
                className="rounded-md border border-gray-200 px-3 py-2 text-sm md:col-span-2"
              />
              <textarea
                value={customEntityForm.fieldSchemaJson}
                onChange={(event) =>
                  setCustomEntityForm((current) => ({ ...current, fieldSchemaJson: event.target.value }))
                }
                rows={5}
                className="rounded-md border border-gray-200 px-3 py-2 font-mono text-xs md:col-span-2"
              />
              <div className="md:col-span-2 flex items-center justify-between gap-3">
                <div className="text-xs text-rose-500">{customEntityError}</div>
                <button className="rounded-md bg-gray-900 px-4 py-2 text-sm font-medium text-white hover:bg-gray-800">
                  Create entity
                </button>
              </div>
            </form>

            <div className="space-y-3">
              {customEntityDefinitions.length === 0 ? (
                <div className="rounded-lg border border-dashed border-gray-200 px-4 py-6 text-sm text-gray-500">
                  No custom entities yet.
                </div>
              ) : (
                customEntityDefinitions.map((definition) => (
                  <div key={definition.id} className="rounded-lg border border-gray-200 px-4 py-4">
                    <div className="flex flex-wrap items-start justify-between gap-3">
                      <div>
                        <div className="text-sm font-semibold text-gray-900">{definition.name}</div>
                        <div className="mt-1 text-xs text-gray-500">
                          {definition.apiName} · {definition.pluralLabel ?? "No plural label"} · {definition.active ? "Active" : "Inactive"}
                        </div>
                        <div className="mt-2 rounded-md bg-gray-50 px-3 py-2 font-mono text-xs text-gray-600">
                          {definition.fieldSchemaJson}
                        </div>
                      </div>
                      <div className="flex flex-col items-end gap-2">
                        <div className="text-xs text-gray-400">{formatDate(definition.createdAt)}</div>
                        <button
                          onClick={() => void openRecords(definition.id)}
                          className="rounded-md border border-gray-200 px-3 py-1.5 text-xs font-medium text-gray-700 hover:bg-gray-50"
                        >
                          {loadingRecordsFor === definition.id ? "Loading..." : "View records"}
                        </button>
                      </div>
                    </div>
                  </div>
                ))
              )}
            </div>

            {selectedDefinitionId ? (
              <div className="mt-5 rounded-lg border border-gray-200 bg-gray-50 px-4 py-4">
                <div className="mb-2 text-sm font-semibold text-gray-900">Records for definition #{selectedDefinitionId}</div>
                {recordError ? <div className="mb-2 text-xs text-rose-500">{recordError}</div> : null}
                {selectedDefinitionRecords.length === 0 ? (
                  <div className="text-sm text-gray-500">No records found for this custom entity yet.</div>
                ) : (
                  <div className="space-y-2">
                    {selectedDefinitionRecords.map((record) => (
                      <div key={record.id} className="rounded-md border border-gray-200 bg-white px-3 py-3">
                        <div className="mb-1 text-xs text-gray-400">#{record.id} · {formatDate(record.createdAt)}</div>
                        <div className="font-mono text-xs text-gray-700">{record.recordDataJson}</div>
                      </div>
                    ))}
                  </div>
                )}
              </div>
            ) : null}
          </section>

          <section className="rounded-xl border border-gray-200 bg-white p-5">
            <div className="mb-4 flex items-center justify-between">
              <div>
                <h2 className="text-base font-semibold text-gray-900">Workflow Builder</h2>
                <p className="text-sm text-gray-500">Use platform metadata to create trigger-action workflows, including custom entity targets.</p>
              </div>
              <div className="rounded-full bg-gray-100 px-3 py-1 text-xs font-medium text-gray-600">
                {workflowDefinitions.length} workflows
              </div>
            </div>

            <form onSubmit={submitWorkflow} className="grid gap-3 md:grid-cols-2">
              <input
                value={workflowForm.name}
                onChange={(event) => setWorkflowForm((current) => ({ ...current, name: event.target.value }))}
                placeholder="Workflow name"
                className="rounded-md border border-gray-200 px-3 py-2 text-sm md:col-span-2"
              />
              <select
                value={workflowForm.triggerType}
                onChange={(event) => setWorkflowForm((current) => ({ ...current, triggerType: event.target.value }))}
                className="rounded-md border border-gray-200 px-3 py-2 text-sm"
              >
                {(workflowCatalog?.triggerTypes ?? []).map((triggerType) => (
                  <option key={triggerType} value={triggerType}>
                    {triggerType}
                  </option>
                ))}
              </select>
              <select
                value={workflowForm.targetEntityType}
                onChange={(event) => setWorkflowForm((current) => ({ ...current, targetEntityType: event.target.value }))}
                className="rounded-md border border-gray-200 px-3 py-2 text-sm"
              >
                {(workflowCatalog?.targetEntityTypes ?? []).map((targetEntityType) => (
                  <option key={targetEntityType} value={targetEntityType}>
                    {targetEntityType}
                  </option>
                ))}
              </select>
              {workflowForm.targetEntityType === "CUSTOM_ENTITY" ? (
                <select
                  value={workflowForm.targetEntityApiName}
                  onChange={(event) =>
                    setWorkflowForm((current) => ({ ...current, targetEntityApiName: event.target.value }))
                  }
                  className="rounded-md border border-gray-200 px-3 py-2 text-sm md:col-span-2"
                >
                  <option value="">Select custom entity</option>
                  {(workflowCatalog?.customEntities ?? []).map((entity) => (
                    <option key={entity.apiName} value={entity.apiName}>
                      {entity.name} ({entity.apiName})
                    </option>
                  ))}
                </select>
              ) : null}
              <select
                value={workflowForm.actionType}
                onChange={(event) => setWorkflowForm((current) => ({ ...current, actionType: event.target.value }))}
                className="rounded-md border border-gray-200 px-3 py-2 text-sm"
              >
                {(workflowCatalog?.actionTypes ?? []).map((actionType) => (
                  <option key={actionType} value={actionType}>
                    {actionType}
                  </option>
                ))}
              </select>
              <input
                value={workflowForm.actionSubject}
                onChange={(event) => setWorkflowForm((current) => ({ ...current, actionSubject: event.target.value }))}
                placeholder="Action subject"
                className="rounded-md border border-gray-200 px-3 py-2 text-sm"
              />
              <textarea
                value={workflowForm.actionDetails}
                onChange={(event) => setWorkflowForm((current) => ({ ...current, actionDetails: event.target.value }))}
                rows={3}
                placeholder="Action details"
                className="rounded-md border border-gray-200 px-3 py-2 text-sm md:col-span-2"
              />
              <div className="md:col-span-2 flex items-center justify-between gap-3">
                <div className="text-xs text-rose-500">{workflowError}</div>
                <button className="rounded-md bg-gray-900 px-4 py-2 text-sm font-medium text-white hover:bg-gray-800">
                  Create workflow
                </button>
              </div>
            </form>

            <div className="mt-5 space-y-3">
              {workflowDefinitions.length === 0 ? (
                <div className="rounded-lg border border-dashed border-gray-200 px-4 py-6 text-sm text-gray-500">
                  No workflows configured yet.
                </div>
              ) : (
                workflowDefinitions.map((workflow) => (
                  <div key={workflow.id} className="rounded-lg border border-gray-200 px-4 py-4">
                    <div className="flex flex-wrap items-start justify-between gap-3">
                      <div>
                        <div className="text-sm font-semibold text-gray-900">{workflow.name}</div>
                        <div className="mt-1 text-xs text-gray-500">
                          {workflow.triggerType} → {workflow.actionType}
                        </div>
                        <div className="mt-2 text-xs text-gray-600">
                          Target: {workflow.targetEntityType ?? "Any"} {workflow.targetEntityApiName ? `(${workflow.targetEntityApiName})` : ""}
                        </div>
                      </div>
                      <div className="text-xs text-gray-400">{formatDate(workflow.createdAt)}</div>
                    </div>
                  </div>
                ))
              )}
            </div>
          </section>
        </div>

        <section className="rounded-xl border border-gray-200 bg-white p-5">
          <div className="mb-4 flex items-center justify-between">
            <div>
              <h2 className="text-base font-semibold text-gray-900">Integration Marketplace</h2>
              <p className="text-sm text-gray-500">Install curated apps into tenant-scoped connections.</p>
            </div>
            <div className="rounded-full bg-gray-100 px-3 py-1 text-xs font-medium text-gray-600">
              {integrations.length} installed
            </div>
          </div>

          {marketplaceError ? <div className="mb-4 text-xs text-rose-500">{marketplaceError}</div> : null}

          <div className="space-y-3">
            {marketplaceSummary.map((app) => (
              <div key={app.appKey} className="rounded-lg border border-gray-200 px-4 py-4">
                <div className="flex items-start justify-between gap-3">
                  <div>
                    <div className="text-sm font-semibold text-gray-900">{app.name}</div>
                    <div className="mt-1 text-xs text-gray-500">
                      {app.category} · {app.channelType} · {app.provider} · v{app.version}
                    </div>
                    <p className="mt-2 text-sm text-gray-600">{app.summary}</p>
                    <div className="mt-3 flex flex-wrap gap-2">
                      {app.capabilities.map((capability) => (
                        <span key={capability} className="rounded-full bg-gray-100 px-2.5 py-1 text-[11px] font-medium text-gray-600">
                          {capability}
                        </span>
                      ))}
                    </div>
                  </div>
                  <button
                    onClick={() => void installApp(app.appKey, app.name)}
                    disabled={app.installed}
                    className={`rounded-md px-3 py-2 text-xs font-medium ${
                      app.installed
                        ? "cursor-not-allowed bg-gray-100 text-gray-400"
                        : "bg-gray-900 text-white hover:bg-gray-800"
                    }`}
                  >
                    {app.installed ? "Installed" : "Install"}
                  </button>
                </div>
              </div>
            ))}
          </div>

          <div className="mt-6 border-t border-gray-200 pt-4">
            <div className="mb-3 text-sm font-semibold text-gray-900">Installed Connections</div>
            <div className="space-y-3">
              {integrations.length === 0 ? (
                <div className="rounded-lg border border-dashed border-gray-200 px-4 py-6 text-sm text-gray-500">
                  No installed integrations yet.
                </div>
              ) : (
                integrations.map((connection) => (
                  <div key={connection.id} className="rounded-lg border border-gray-200 px-4 py-4">
                    <div className="flex items-start justify-between gap-3">
                      <div>
                        <div className="text-sm font-semibold text-gray-900">{connection.name}</div>
                        <div className="mt-1 text-xs text-gray-500">
                          {connection.channelType} · {connection.provider} · {connection.status}
                        </div>
                        {connection.marketplaceAppKey ? (
                          <div className="mt-2 text-xs text-gray-600">
                            Source app: {connection.marketplaceAppKey} · v{connection.marketplaceVersion ?? "n/a"}
                          </div>
                        ) : null}
                      </div>
                      <div className="text-xs text-gray-400">{formatDate(connection.createdAt)}</div>
                    </div>
                  </div>
                ))
              )}
            </div>
          </div>
        </section>
      </div>
    </div>
  );
}
