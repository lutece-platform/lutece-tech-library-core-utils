# Library Core Utils

This library objective is to decouple some utilitary classes related to XML, security or paths to allow plugins or libs to use those classes without the need to depend on lutece-core.
## Configuration

### Client IP address behind reverse proxies

`SecurityUtil.getRealIp( request )` reads the client address from the `X-Forwarded-For` header.

- `lutece.security.trustedProxies` not set or empty (default): the header is trusted whatever the sender of the request.
- `lutece.security.trustedProxies` set: a comma-separated list of the reverse proxies of the site, as addresses or CIDR ranges, IPv4 or IPv6, for instance `lutece.security.trustedProxies=10.0.0.0/8, 192.168.1.10, fd00::/8`. The header is then used only when the request comes from one of these proxies (`request.getRemoteAddr( )`), and the client address is the right-most address of the header that is not a trusted proxy (the left-most one when the header holds trusted proxies only). A request from any other address gets its own address: a forged header is ignored.

Set it on every site behind a reverse proxy: the login lock (and every feature based on the client address) can otherwise be bypassed, or turned against another address, with a forged header.
