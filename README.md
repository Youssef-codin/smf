## OpenAPI Documentation
To view and test the API endpoints, open the following endpoint in your browser or API client: `/api-docs`

	
## Tools
- **Java Version:** 21  
- **Build Tool:** Maven  
- **Database:** PostgreSQL  
	
---
	
## Development Standards
	
1. All API responses **must use the `ApiResponse` class**.
2. All controllers **must be mapped using the `/api` prefix**.
3. All exceptions **must be handled by the `GlobalExceptionHandler`**.
4. All work **must be done in a new branch** (never commit directly to `main`).
5. Commit messages must be **clear and descriptive**  
	- Example: `feature: add device registration`
6. After finishing a feature, **contact Youssef for code review** before merging into `main`.
	
---
