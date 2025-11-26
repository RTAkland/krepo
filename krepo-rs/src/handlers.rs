/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 11/26/25
 */

use crate::auth::check_basic_auth;
use crate::models::User;
use actix_web::{http::header, web, HttpRequest, HttpResponse, Responder};
use std::fs;
use std::path::{Path, PathBuf};
use std::sync::Arc;

pub struct AppState {
    pub users: Vec<User>,
    pub base_path: PathBuf,
}

pub async fn upload_file(
    req: HttpRequest,
    data: web::Data<Arc<AppState>>,
    path: web::Path<(String, String)>,
    body: web::Bytes,
) -> impl Responder {
    let (repo, tail) = path.into_inner();
    if repo != "releases" && repo != "snapshots" && repo != "private" {
        return HttpResponse::BadRequest().body("Invalid repository");
    }
    if !check_basic_auth(&req, &data.users) {
        return HttpResponse::Unauthorized()
            .append_header((
                header::WWW_AUTHENTICATE,
                "Basic realm=\"Simple Maven Repo\"",
            ))
            .finish();
    }
    let tail_path = Path::new(&tail);
    let filename = tail_path.file_name().unwrap().to_str().unwrap();
    let group_path = tail_path.parent().unwrap_or_else(|| Path::new(""));
    let save_dir = data.base_path.join(&repo).join(group_path);
    if let Err(e) = fs::create_dir_all(&save_dir) {
        return HttpResponse::InternalServerError()
            .body(format!("Failed to create directory: {}", e));
    }
    let save_path = save_dir.join(filename);
    match fs::write(save_path, &body) {
        Ok(_) => HttpResponse::Ok().body("Upload successful"),
        Err(e) => HttpResponse::InternalServerError().body(format!("Failed to save file: {}", e)),
    }
}

pub async fn download_file(
    req: HttpRequest,
    data: web::Data<Arc<AppState>>,
    path: web::Path<(String, String)>,
) -> impl Responder {
    let (repo, tail) = path.into_inner();
    let users = &data.users;
    if repo != "releases" && repo != "snapshots" && repo != "private" {
        return HttpResponse::BadRequest().body("Invalid repository");
    }
    if repo == "private" && !check_basic_auth(&req, users) {
        return HttpResponse::Unauthorized()
            .append_header((
                header::WWW_AUTHENTICATE,
                "Basic realm=\"Simple Maven Repo\"",
            ))
            .finish();
    }
    let tail_path = Path::new(&tail);
    let filename = match tail_path.file_name() {
        Some(name) => name.to_str().unwrap_or("unknown"),
        None => return HttpResponse::BadRequest().body("Invalid file path"),
    };
    let parent_dir = tail_path.parent().unwrap_or_else(|| Path::new(""));
    let file_path = data.base_path.join(&repo).join(parent_dir).join(filename);
    match tokio::fs::read(&file_path).await {
        Ok(content) => HttpResponse::Ok()
            .append_header((
                "Content-Disposition",
                format!("attachment; filename=\"{}\"", filename),
            ))
            .body(content),
        Err(_) => HttpResponse::NotFound().body("File not found"),
    }
}
