/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 11/26/25
 */

use crate::models::User;
use actix_web::http::header;
use actix_web::HttpRequest;
use base64::engine::general_purpose;
use base64::Engine;

pub fn check_basic_auth(req: &HttpRequest, users: &[User]) -> bool {
    if let Some(auth_header) = req.headers().get(header::AUTHORIZATION) {
        if let Ok(auth_str) = auth_header.to_str() {
            if auth_str.starts_with("Basic ") {
                let base64encoded = &auth_str[6..];
                if let Ok(decoded) = general_purpose::STANDARD.decode(base64encoded) {
                    if let Ok(decoded_str) = String::from_utf8(decoded) {
                        let parts: Vec<&str> = decoded_str.splitn(2, ':').collect();
                        if parts.len() == 2 {
                            let (user, pass) = (parts[0], parts[1]);
                            for u in users {
                                if u.name == user && u.password == pass {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    false
}
