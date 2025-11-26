/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 11/26/25
 */

mod auth;
mod handlers;
mod models;

use actix_web::{main, web, App, HttpServer};
use handlers::{download_file, upload_file, AppState};
use serde_json::from_str;
use std::path::PathBuf;
use std::sync::Arc;

#[main]
async fn main() -> std::io::Result<()> {
    let users_json_path = "./data/users.json";
    let users = {
        let content = std::fs::read_to_string(users_json_path).expect("Failed to read users JSON");
        from_str(&content).expect("Failed to parse users JSON")
    };
    let base_path = PathBuf::from("./repositories");
    let app_state = Arc::new(AppState { users, base_path });
    println!("Server running on http://127.0.0.1:8080");
    HttpServer::new(move || {
        App::new()
            .app_data(web::Data::new(app_state.clone()))
            .route("/{repo}/{tail:.*}", web::put().to(upload_file))
            .route("/{repo}/{tail:.*}", web::get().to(download_file))
    })
    .bind(("127.0.0.1", 8080))?
    .run()
    .await
}
