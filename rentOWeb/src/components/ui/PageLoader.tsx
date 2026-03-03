"use client";

export default function PageLoader({ message = "Loading..." }: { message?: string }) {
    return (
        <div className="rento-loader">
            <div className="rento-spinner" />
            <p className="text-sm font-medium text-[var(--t2)] tracking-wide">{message}</p>
        </div>
    );
}

export function TableSkeleton({ rows = 5, cols = 4 }: { rows?: number; cols?: number }) {
    return (
        <div className="space-y-3 p-4">
            {/* Header skeleton */}
            <div className="flex gap-4 mb-4">
                {Array.from({ length: cols }).map((_, i) => (
                    <div key={i} className="skeleton h-4 flex-1" style={{ animationDelay: `${i * 100}ms` }} />
                ))}
            </div>
            {/* Row skeletons */}
            {Array.from({ length: rows }).map((_, r) => (
                <div key={r} className="flex gap-4 items-center" style={{ animationDelay: `${r * 80}ms` }}>
                    {Array.from({ length: cols }).map((_, c) => (
                        <div key={c} className="skeleton h-10 flex-1 rounded-lg" />
                    ))}
                </div>
            ))}
        </div>
    );
}
