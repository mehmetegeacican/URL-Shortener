---
name: Custom Short Link Support
about: Implement support for user-defined short codes and validation
title: "Feature: Custom Short Link Support"
labels: feature, enhancement, backend
assignees: mehmetegeacican
---

## Description
Allow users to specify a custom short link code when creating a shortened URL instead of only relying on system-generated codes.

## Functional Requirements
- [ ] Users can optionally provide a custom short code during URL creation
- [ ] The system validates that the custom code is unique
- [ ] Invalid custom codes are rejected with a clear validation message
- [ ] If the user does not provide a custom code, the system generates one automatically
- [ ] The custom code is preserved when redirecting by short URL

## Acceptance Criteria
- [ ] API request accepts an optional `code` field
- [ ] Duplicate custom codes return an error response
- [ ] Invalid characters or reserved values are rejected
- [ ] Existing redirect flow still works for auto-generated codes
- [ ] Unit tests written for duplicate and valid custom code scenarios

## Implementation Notes
- Add validation in the URL creation flow before saving the entity
- Ensure uniqueness is checked against database records
- Keep the same redirect endpoint logic (`/{code}`)
- Consider adding a maximum length for custom codes
- Return `409 Conflict` for duplicate codes and `400 Bad Request` for invalid format

## Related Issues
N/A
