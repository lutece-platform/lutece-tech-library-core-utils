# Library Core Utils

This library objective is to decouple some utilitary classes related to XML, security or paths to allow plugins or libs to use those classes without the need to depend on lutece-core.
## Configuration

### Client IP address behind reverse proxies

`SecurityUtil.getRealIp( request )` reads the client address from the `X-Forwarded-For` header.

- `lutece.security.trustedProxies` not set or empty (default): the header is ignored, the client address is the address the request comes from (`request.getRemoteAddr( )`). Since LUT-35688: before, the header was trusted whatever the sender, so any client could choose its address.
- `lutece.security.trustedProxies` set: a comma-separated list of the reverse proxies of the site, as addresses or CIDR ranges, IPv4 or IPv6, for instance `lutece.security.trustedProxies=10.0.0.0/8, 192.168.1.10, fd00::/8`. The header is then used only when the request comes from one of these proxies (`request.getRemoteAddr( )`), and the client address is the right-most address of the header that is not a trusted proxy (the left-most one when the header holds trusted proxies only). A request from any other address gets its own address: a forged header is ignored.

Set it on every site behind a reverse proxy: without it, every request is seen with the address of the proxy, so the login lock by address and every feature based on the client address apply to all the users of the proxy at once.
