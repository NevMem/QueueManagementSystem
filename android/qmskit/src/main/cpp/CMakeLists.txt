cmake_minimum_required(VERSION 3.10.2)

project("qmskit")

add_library(
    qmskit
    SHARED
    qmskit.cpp)

find_library(
    log-lib
    log)

target_link_libraries(
    qmskit
    ${log-lib})
