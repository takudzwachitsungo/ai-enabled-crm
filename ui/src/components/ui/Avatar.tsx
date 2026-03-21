import React from 'react';
interface AvatarProps {
  src?: string;
  fallback: string;
  size?: 'sm' | 'md' | 'lg' | 'xl';
  className?: string;
}
export function Avatar({
  src,
  fallback,
  size = 'md',
  className = ''
}: AvatarProps) {
  const sizeClasses = {
    sm: 'w-5 h-5 text-[10px]',
    md: 'w-6 h-6 text-xs',
    lg: 'w-8 h-8 text-sm',
    xl: 'w-16 h-16 text-xl'
  };
  return (
    <div
      className={`relative inline-flex items-center justify-center rounded-full bg-gray-200 text-gray-600 font-medium overflow-hidden shrink-0 ${sizeClasses[size]} ${className}`}>
      
      {src ?
      <img src={src} alt={fallback} className="w-full h-full object-cover" /> :

      <span>{fallback.charAt(0).toUpperCase()}</span>
      }
    </div>);

}