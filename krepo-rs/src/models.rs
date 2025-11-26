/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 11/26/25
 */

use serde::Deserialize;

#[derive(Debug, Deserialize)]
pub struct User {
    pub name: String,
    #[allow(dead_code)]
    pub email: String,
    pub password: String,
    #[allow(dead_code)]
    pub uid: String,
}
