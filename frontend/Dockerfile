# Use an official Node.js runtime as a parent image
FROM node:lts-alpine

# Set the working directory in the container
WORKDIR /frontend

# Copy package.json and package-lock.json
COPY package.json pnpm-lock.yaml ./

# Install any needed packages specified in package.json
RUN npm install 

# Copy the dorest of the application code
COPY . ./

# Make port 5173 available to the world outside this container
EXPOSE 5173

# Define environment variable
#ENV NODE_ENV=production

# Run the application using node
CMD ["npm", "run", "dev", "--", "--host"]
