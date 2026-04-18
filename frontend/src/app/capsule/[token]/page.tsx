// Placeholder capsule viewer — time-lock unlock flow to be implemented later.
export default function CapsuleViewerPage({
  params,
}: {
  params: { token: string }
}) {
  return (
    <main>
      <h1>Opening Capsule…</h1>
      <p>Token: {params.token}</p>
    </main>
  )
}
