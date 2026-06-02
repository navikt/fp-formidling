# fp-formidling

Orchestrates ordering and production of letters and documents for foreldrepenger, svangerskapspenger and engangsstonad.

## Shared context

- Source of truth for shared domain, architecture, and conventions: `navikt/fp-context`
- Copilot Space: `navikt/TeamForeldrepenger`

## Repo-specific context

| Topic | Details                                                                                                  |
|---|----------------------------------------------------------------------------------------------------------|
| Role | Produces, journals and distributes letters triggered from `fp-sak`. Also generates pdf and html previews |
| Consumers | `fp-sak`                                                                                                 |
| Tech stack | Standard fp Java backend  using `fp-prosesstask`                                                         |
| Main integrations | `fp-sak`, `fp-dokgen`, Joark (DokArkiv, DokDist), PDL                                                    |
| Data | PostgreSQL temporary storage during letter production until ack sent to `fp-sak`                         |

## Entry points

BrevRestTjeneste will call back to `fp-sak` for details and call `fp-dokgen` to produce PDF or HTML letter
- bestill: Produces PDF letter, journals and distributes this before sending confirmation fo `fp-sak`.
- forhaandsvis: returns PDF preview document
- generer: return HTML preview for further editing in `fp-sak`

## Verification

- For integration impact, verify via `navikt/fp-autotest`.
- Most relevant suite: `verdikjede`.
