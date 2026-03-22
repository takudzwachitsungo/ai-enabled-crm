import React, { useMemo, useState } from "react";
import { BookOpenIcon, MailIcon, PackageIcon, SearchIcon } from "lucide-react";
import {
  CannedResponseRecord,
  KnowledgeBaseArticleRecord,
  ProductRecord,
} from "../types/crm";

interface EmailTemplatesListProps {
  knowledgeArticles?: KnowledgeBaseArticleRecord[] | null;
  cannedResponses?: CannedResponseRecord[] | null;
  products?: ProductRecord[] | null;
}

function formatDate(value: string) {
  return new Date(value).toLocaleDateString();
}

function formatMoney(value: number) {
  return new Intl.NumberFormat("en-US", {
    style: "currency",
    currency: "USD",
    maximumFractionDigits: 0,
  }).format(value);
}

export function EmailTemplatesList({
  knowledgeArticles = [],
  cannedResponses = [],
  products = [],
}: EmailTemplatesListProps) {
  const [query, setQuery] = useState("");

  const filteredKnowledge = useMemo(
    () =>
      knowledgeArticles.filter((item) =>
        [item.title, item.category, item.body].join(" ").toLowerCase().includes(query.toLowerCase()),
      ),
    [knowledgeArticles, query],
  );

  const filteredResponses = useMemo(
    () =>
      cannedResponses.filter((item) =>
        [item.title, item.category ?? "", item.channelType, item.body]
          .join(" ")
          .toLowerCase()
          .includes(query.toLowerCase()),
      ),
    [cannedResponses, query],
  );

  const filteredProducts = useMemo(
    () =>
      products.filter((item) =>
        [item.name, item.description ?? "", item.status].join(" ").toLowerCase().includes(query.toLowerCase()),
      ),
    [products, query],
  );

  return (
    <div className="flex min-h-0 flex-1 flex-col overflow-hidden bg-[#f8f9fa]">
      <div className="shrink-0 border-b border-gray-200 bg-white px-4 py-4 sm:px-6">
        <div className="flex items-center justify-between gap-3">
          <div className="flex items-center gap-2 text-lg">
            <span className="text-gray-500">Library /</span>
            <span className="font-semibold text-gray-900">Knowledge & Catalog</span>
          </div>
          <div className="rounded-md border border-gray-200 bg-gray-50 px-3 py-1.5 text-sm text-gray-500">
            {knowledgeArticles.length} articles · {cannedResponses.length} responses · {products.length} products
          </div>
        </div>
      </div>

      <div className="shrink-0 border-b border-gray-200 bg-white px-4 py-3 sm:px-6">
        <div className="relative max-w-md">
          <SearchIcon className="pointer-events-none absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-gray-400" />
          <input
            value={query}
            onChange={(event) => setQuery(event.target.value)}
            placeholder="Search articles, responses, and products"
            className="w-full rounded-md border border-gray-200 bg-gray-50 py-2 pl-9 pr-3 text-sm text-gray-700"
          />
        </div>
      </div>

      <div className="min-h-0 flex-1 overflow-auto">
        <div className="mx-auto grid max-w-7xl grid-cols-1 gap-6 p-4 sm:p-6 xl:grid-cols-[1.2fr_1fr]">
          <div className="space-y-6">
            <div className="rounded-xl border border-gray-200 bg-white shadow-sm">
              <div className="flex items-center justify-between border-b border-gray-100 px-5 py-4">
                <div className="flex items-center gap-2 font-semibold text-gray-900">
                  <BookOpenIcon className="h-4 w-4 text-gray-400" />
                  Knowledge base
                </div>
              </div>
              <div className="divide-y divide-gray-100">
                {filteredKnowledge.length === 0 ? (
                  <div className="px-5 py-10 text-center text-sm text-gray-500">No knowledge articles match the current search.</div>
                ) : (
                  filteredKnowledge.map((article) => (
                    <div key={article.id} className="px-5 py-4 hover:bg-gray-50">
                      <div className="flex items-start justify-between gap-4">
                        <div className="min-w-0">
                          <div className="text-sm font-medium text-gray-900">{article.title}</div>
                          <div className="mt-1 text-xs text-gray-500">{article.category} · {article.published ? "Published" : "Draft"}</div>
                          <p className="mt-2 line-clamp-3 text-sm text-gray-700">{article.body}</p>
                        </div>
                        <div className="text-xs text-gray-500">{formatDate(article.createdAt)}</div>
                      </div>
                    </div>
                  ))
                )}
              </div>
            </div>

            <div className="rounded-xl border border-gray-200 bg-white shadow-sm">
              <div className="flex items-center justify-between border-b border-gray-100 px-5 py-4">
                <div className="flex items-center gap-2 font-semibold text-gray-900">
                  <MailIcon className="h-4 w-4 text-gray-400" />
                  Canned responses
                </div>
              </div>
              <div className="overflow-x-auto">
                <table className="w-full whitespace-nowrap text-left text-sm">
                  <thead className="bg-gray-50 text-xs text-gray-500">
                    <tr>
                      <th className="px-5 py-3 font-medium">Title</th>
                      <th className="px-5 py-3 font-medium">Category</th>
                      <th className="px-5 py-3 font-medium">Channel</th>
                      <th className="px-5 py-3 font-medium">Updated</th>
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-gray-100">
                    {filteredResponses.map((response) => (
                      <tr key={response.id} className="hover:bg-gray-50">
                        <td className="px-5 py-3">
                          <div className="font-medium text-gray-900">{response.title}</div>
                          <div className="mt-1 max-w-md truncate text-xs text-gray-500">{response.body}</div>
                        </td>
                        <td className="px-5 py-3 text-gray-600">{response.category ?? "-"}</td>
                        <td className="px-5 py-3 text-gray-600">{response.channelType}</td>
                        <td className="px-5 py-3 text-gray-500">{formatDate(response.createdAt)}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
                {filteredResponses.length === 0 ? (
                  <div className="px-5 py-10 text-center text-sm text-gray-500">No canned responses match the current search.</div>
                ) : null}
              </div>
            </div>
          </div>

          <div className="rounded-xl border border-gray-200 bg-white shadow-sm">
            <div className="flex items-center justify-between border-b border-gray-100 px-5 py-4">
              <div className="flex items-center gap-2 font-semibold text-gray-900">
                <PackageIcon className="h-4 w-4 text-gray-400" />
                Product catalog
              </div>
            </div>
            <div className="divide-y divide-gray-100">
              {filteredProducts.length === 0 ? (
                <div className="px-5 py-10 text-center text-sm text-gray-500">No products match the current search.</div>
              ) : (
                filteredProducts.map((product) => (
                  <div key={product.id} className="px-5 py-4 hover:bg-gray-50">
                    <div className="flex items-start justify-between gap-4">
                      <div className="min-w-0">
                        <div className="text-sm font-medium text-gray-900">{product.name}</div>
                        <div className="mt-1 text-xs text-gray-500">{product.status} · {formatMoney(product.unitPrice)}</div>
                        <p className="mt-2 text-sm text-gray-700">{product.description || "No product description provided."}</p>
                      </div>
                      <div className="text-xs text-gray-500">{formatDate(product.createdAt)}</div>
                    </div>
                  </div>
                ))
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
