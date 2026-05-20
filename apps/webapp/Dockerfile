# Use an official Node.js runtime as a parent image
FROM node:lts-alpine

# Set the working directory in the container
WORKDIR /webapp

RUN npm install -g pnpm

# Copy manifests first for layer caching
COPY package.json pnpm-lock.yaml ./

# Install dependencies using pnpm (respects pnpm-lock.yaml)
RUN pnpm install --frozen-lockfile

# Copy the dorest of the application code
COPY . ./

# Make port 5173 available to the world outside this container
EXPOSE 5173

# Define environment variable
#ENV NODE_ENV=production

# Run the application using node
CMD ["npm", "run", "dev", "--", "--host"]
