import React, { useState, useEffect, useCallback } from "react";
import "./App.css";
import axios from "axios";
import { useDropzone } from "react-dropzone";

const UserProfiles = () => {
  const [userProfileState, setUserProfileState] = useState([]);

  const fetchUserProfiles = () => {
    axios.get("http://localhost:8080/api/v1/user-profile").then((res) => {
      console.log(res);
      setUserProfileState(res.data);
    });
  };

  useEffect(() => {
    fetchUserProfiles();
  }, []);

  return userProfileState.map((userProfile, index) => {
    return (
      <div className="User-Profile" key={index}>
        {userProfile.userProfileId ? (
          <img
            src={`http://localhost:8080/api/v1/user-profile/${userProfile.userProfileId}/image/download`}
          />
        ) : null}
        <h1>{userProfile.username}</h1>
        <p>{userProfile.userProfileId}</p>
        <Dropzone {...userProfile} />
      </div>
    );
  });
};

function Dropzone({ userProfileId }) {
  const onDrop = useCallback((acceptedFiles) => {
    const file = acceptedFiles[0];
    console.log(file);
    const formData = new FormData();
    formData.append("file", file); // moet hetzelfde zijn als de RequestParam parameter

    axios
      .post(
        `http://localhost:8080/api/v1/user-profile/${userProfileId}/image/upload`,
        formData,
        {
          headers: {
            "Content-Type": "multipart/form-data",
          },
        }
      )
      .then(() => {
        console.log("file uploaded successfully");
        window.location.reload();
      })
      .catch((err) => {
        console.log(err);
      });
  }, []);
  const { getRootProps, getInputProps, isDragActive } = useDropzone({ onDrop });

  return (
    <div {...getRootProps()}>
      <input {...getInputProps()} />
      {isDragActive ? (
        <p className="Drag-Active">
          Drag 'n' drop some files here, or click to select files
        </p>
      ) : (
        <p className="Drag-Inactive">
          Drag 'n' drop some files here, or click to select files
        </p>
      )}
    </div>
  );
}

function App() {
  return (
    <div className="App">
      <div className="App-header">
        <UserProfiles />
      </div>
    </div>
  );
}

export default App;
