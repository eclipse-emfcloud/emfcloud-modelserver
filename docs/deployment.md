# Intended Use, Deployment, and Security Considerations

> [!IMPORTANT]
> The EMF.cloud Model Server is a **development-time building block** for creating modeling tools and applications. It is designed to run **locally, driven by a trusted client, inside a trusted environment**. It is **not** a turnkey, multi-tenant, production-hardened server, and it is **not** intended to be exposed directly to untrusted networks or clients.
>
> If you deploy it outside this intended context, **you are responsible** for adding the protections described in [Deploying responsibly](#deploying-responsibly).

## What the Model Server is

The Model Server is a backend **component** for building modeling tools. It exposes model access and manipulation (CRUD, commands, undo/redo, validation, change subscriptions) over HTTP and WebSocket, so that a client application (for example a [GLSP](https://eclipse.dev/glsp/)- or [Theia](https://theia-ide.org/)-based modeling tool) can be built on top of it.

It is intended to be:

- **embedded in or driven by a trusted client application**, typically running on the same machine or within a single controlled environment;
- operated by a **single user or a single trusted tool instance** at a time;
- used as a **framework and reference implementation** that adopters extend with their own metamodels, commands, and packaging.

## What the Model Server is not

It is deliberately **not**:

- a public-facing or internet-exposed service;
- a multi-tenant or multi-user server that isolates clients from one another;
- an authentication or authorization service;
- a sandbox that defends against malicious models, payloads, or clients.

## Security model: a trusted environment

Like other tools designed for local use, for example [Redis, which "is designed to be accessed by trusted clients inside trusted environments"](https://redis.io/docs/latest/operate/oss_and_stack/management/security/), the Model Server assumes a **trusted client** and a **trusted workspace**, reached over a **trusted (local) network**.

Within that trust boundary it **deliberately performs no**:

- authentication or authorization of callers;
- network-level access control;
- sandboxing or validation of otherwise well-formed input against malicious intent;
- confinement of file access beyond the operating-system permissions of the process.

As a consequence, and **by design**, a client that can reach the server can, among other things, read and write files that the server process is permitted to access, (re)configure the workspace location, and load and parse model content in the supported formats. These are **intended capabilities** of a local modeling backend, not security weaknesses, and they rely entirely on the deployment keeping untrusted parties *outside* the trust boundary.

The server runs with the privileges of the operating-system user that launched it and can access whatever that user can access.

## Development-oriented defaults

To keep local development and browser-based tooling frictionless, the example launcher and standalone JAR:

- **bind to all network interfaces**;
- **enable permissive CORS** (`Access-Control-Allow-Origin: *`);
- register **no authentication** on any route.

These defaults are appropriate for **local, single-user development on a trusted machine only**. They are not safe for any deployment that is reachable by untrusted networks, browsers, or users.

## Deploying responsibly

If you run the Model Server in any context beyond local, trusted, single-user development, treat it as an **unauthenticated backend that must never be directly reachable by untrusted parties**, and supply the protections it does not provide itself. Recommended layers (defense in depth):

- **Network isolation**: keep the server on a private network segment or the loopback interface, and **firewall the port** so it is not reachable from untrusted networks; never expose it to the public internet. *(The launcher currently exposes only a `-p`/`--port` option and no host-binding option, so restricting the listener to loopback requires a firewall, a reverse proxy, a container network namespace, or a customized launcher.)*
- **Reverse proxy with authentication and TLS**: place the server behind a proxy (e.g. nginx, Apache httpd, or an API gateway) that terminates TLS and enforces authentication and authorization. The Model Server performs none of these itself.
- **Restrict CORS**: do not rely on the permissive development default; restrict the allowed origins at the proxy for any networked deployment.
- **Least privilege**: run as a dedicated, unprivileged operating-system user; restrict the parts of the filesystem the process can read and write (a dedicated workspace directory, a container, or a sandbox); scope the workspace root to only the directories you intend to serve.
- **Trusted inputs only**: only load workspaces and models that you trust. Model content is parsed and file operations are performed on caller-supplied data without additional sandboxing.

A typical hardened deployment therefore layers: *untrusted network → firewall → private network / reverse proxy → TLS → authentication &amp; authorization → Model Server*.

## A note on these assumptions

This security model reflects the Model Server's intended use as a local development component. Adopters whose deployment differs should **re-evaluate** these assumptions, and it is the adopter's responsibility to add the appropriate controls before deploying outside the intended context.

## Reporting a vulnerability

Please report suspected vulnerabilities through the process described in [`SECURITY.md`](../SECURITY.md). Do not open public issues, pull requests, or discussions for them.

Reports describing behavior that is only reachable when the server is deployed contrary to the guidance above (for example, exposed to untrusted networks or clients) are treated as **deployment-hardening** matters rather than defects in the component. Issues that are exploitable **within** the intended trusted-local usage are handled as described in `SECURITY.md`.
