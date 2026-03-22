import React from 'react';
interface StatusBadgeProps {
  status: string;
  className?: string;
  showText?: boolean;
}
export function StatusBadge({
  status,
  className = '',
  showText = true
}: StatusBadgeProps) {
  let dotColor = 'bg-gray-400';
  let textColor = 'text-gray-700';
  switch (status.toLowerCase()) {
    case 'new':
    case 'draft':
      dotColor = 'bg-slate-400';
      break;
    case 'qualified':
    case 'negotiation':
    case 'paid':
    case 'healthy':
    case 'live':
      dotColor = 'bg-green-500';
      break;
    case 'contacted':
    case 'qualification':
    case 'ready to close':
    case 'watch':
    case 'escalated':
      dotColor = 'bg-orange-500';
      textColor = 'text-orange-600';
      break;
    case 'nurture':
    case 'proposal/quotation':
    case 'approved':
    case 'issued':
      dotColor = 'bg-blue-500';
      break;
    case 'at_risk':
    case 'refunded':
    case 'cancelled':
    case 'expired':
      dotColor = 'bg-rose-500';
      textColor = 'text-rose-700';
      break;
  }
  return (
    <div className={`inline-flex items-center gap-2 ${className}`}>
      <span
        className={`w-2.5 h-2.5 rounded-full ${dotColor} shrink-0 ring-2 ring-white shadow-sm`} />
      
      {showText &&
      <span className={`text-sm font-medium ${textColor}`}>{status}</span>
      }
    </div>);

}
