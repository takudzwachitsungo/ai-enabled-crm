import React from "react";
import { ArrowRightIcon } from "lucide-react";
import { AppView, workspaceSections } from "../data/mockData";

interface ModuleWorkspaceProps {
  view: AppView;
}

export function ModuleWorkspace({ view }: ModuleWorkspaceProps) {
  const section = workspaceSections[view];

  return (
    <section className="rounded-[2rem] border border-[color:var(--border-soft)] bg-[color:var(--surface)] p-6 shadow-[0_24px_60px_rgba(9,20,40,0.06)]">
      <div className="flex flex-col gap-4 border-b border-[color:var(--border-soft)] pb-6 lg:flex-row lg:items-end lg:justify-between">
        <div className="max-w-3xl">
          <p className="text-[11px] font-semibold uppercase tracking-[0.24em] text-[color:var(--ink-subtle)]">
            {section.eyebrow}
          </p>
          <h2 className="mt-2 font-display text-3xl font-semibold text-[color:var(--ink-strong)]">
            {section.title}
          </h2>
          <p className="mt-3 text-sm leading-7 text-[color:var(--ink-muted)]">
            {section.summary}
          </p>
        </div>
        <div className="max-w-sm rounded-3xl bg-[color:var(--surface-alt)] px-5 py-4 text-sm leading-6 text-[color:var(--ink-muted)]">
          {section.callout}
        </div>
      </div>

      <div className="mt-6 overflow-hidden rounded-[1.5rem] border border-[color:var(--border-soft)]">
        <div className="grid grid-cols-4 bg-[color:var(--surface-alt)] px-5 py-3 text-[11px] font-semibold uppercase tracking-[0.18em] text-[color:var(--ink-subtle)]">
          {section.columns.map((column) => (
            <div key={column}>{column}</div>
          ))}
        </div>
        <div className="divide-y divide-[color:var(--border-soft)]">
          {section.rows.map((row, index) => (
            <div
              key={`${section.title}-${index}`}
              className="grid grid-cols-1 gap-4 px-5 py-5 text-sm text-[color:var(--ink-muted)] md:grid-cols-4"
            >
              {section.columns.map((column, columnIndex) => (
                <div key={column}>
                  <div className="md:hidden text-[11px] font-semibold uppercase tracking-[0.18em] text-[color:var(--ink-subtle)]">
                    {column}
                  </div>
                  <div
                    className={`leading-6 ${
                      columnIndex === 0
                        ? "font-semibold text-[color:var(--ink-strong)]"
                        : ""
                    }`}
                  >
                    {row[column]}
                  </div>
                </div>
              ))}
            </div>
          ))}
        </div>
      </div>

      <div className="mt-5 flex items-center justify-end">
        <button className="inline-flex items-center gap-2 rounded-full border border-[color:var(--border-soft)] bg-[color:var(--surface-strong)] px-4 py-2 text-sm font-medium text-[color:var(--ink-strong)] transition hover:border-[color:var(--accent)] hover:text-[color:var(--accent)]">
          Map to backend flows
          <ArrowRightIcon className="h-4 w-4" />
        </button>
      </div>
    </section>
  );
}
